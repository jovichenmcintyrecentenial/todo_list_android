package com.centennial.team_15_mapd_721_todo_app.ui.main

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.centennial.team_15_mapd_721_todo_app.R
import com.centennial.team_15_mapd_721_todo_app.adapters.SwipeToDeleteCallback
import com.centennial.team_15_mapd_721_todo_app.adapters.TaskAdapter
import com.centennial.team_15_mapd_721_todo_app.databinding.ActivityMainBinding
import com.centennial.team_15_mapd_721_todo_app.models.MyConstants
import com.centennial.team_15_mapd_721_todo_app.models.TaskModel
import com.centennial.team_15_mapd_721_todo_app.service.AlarmService
import com.centennial.team_15_mapd_721_todo_app.ui.login.LoginActivity
import com.centennial.team_15_mapd_721_todo_app.ui.task_details.TaskDetailsActivity
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognizerIntent: Intent
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: TaskAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var mainViewModel: MainViewModel? = null
    private var isFABOpen = false


    var list:MutableList<TaskModel>? = null

    private val alarmBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        AlarmService.stopSound()
        if(mainViewModel!=null){
           mainViewModel!!.getTasks(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        //connect to view model
        mainViewModel = ViewModelProvider(this).get(modelClass = MainViewModel::class.java)

        updateTitleDate()

        binding.fabOption2.setOnClickListener {
            startListening()
            closeFABMenu()

        }

        binding.fabOption1.setOnClickListener {
            val intent = Intent(this, TaskDetailsActivity::class.java)
            startActivity(intent)
            closeFABMenu()

        }

        binding.fab.setOnClickListener {
            if (!isFABOpen) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
        }

        AlarmService.initialize(this)

        viewManager = LinearLayoutManager(this)
        recyclerView = binding.recyclerView


        // Initialize the SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        // Set the RecognitionListener to handle the results
        speechRecognizer.setRecognitionListener(recognitionListener)

        // Create the RecognizerIntent
        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        mainViewModel?.liveTaskListData?.observe(this, androidx.lifecycle.Observer { listOfTask ->
            if (listOfTask != null) {
                if(listOfTask.isNotEmpty()){
                    binding.emptyListView.visibility = View.GONE
                }
                else{
                    binding.emptyListView.visibility = View.VISIBLE
                }
                if (list == null) {
                    list = mutableListOf()
                    viewAdapter = TaskAdapter(list!!){ task ->
                        var newIntent = Intent(this, TaskDetailsActivity::class.java)
                        newIntent.putExtra("task", Gson().toJson(task))
                        //load new Intent
                        startActivity(newIntent)
                    }

                    val swipeHandler = object : SwipeToDeleteCallback(viewAdapter) {
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            list!![viewHolder.adapterPosition]

                            when (direction) {
                                ItemTouchHelper.LEFT -> {
                                    // Handle swipe to left
                                    mainViewModel!!.deleteTask( list!![viewHolder.adapterPosition])
                                    viewAdapter.deleteItem(viewHolder.adapterPosition)
                                    if(viewAdapter.itemCount == 0){

                                        binding.emptyListView.visibility = View.VISIBLE
                                    }
                                }
                                ItemTouchHelper.RIGHT -> {
                                    // Handle swipe to right
                                    mainViewModel!!.makeAsCompleted(applicationContext, list!![viewHolder.adapterPosition])
                                    viewAdapter.completeItem(viewHolder.adapterPosition)

                                }
                            }


                        }
                    }

                    val itemTouchHelper = ItemTouchHelper(swipeHandler)
                    itemTouchHelper.attachToRecyclerView(recyclerView)
                }

                list?.apply {
                    clear()
                    addAll(listOfTask)
                }

                if (recyclerView.adapter == null) {
                    recyclerView.apply {
                        setHasFixedSize(true)
                        layoutManager = viewManager
                        adapter = viewAdapter
                    }
                } else {
                    viewAdapter.notifyDataSetChanged()
                }
            }
        })

        mainViewModel?.liveSpeechInterpret?.observe(this, androidx.lifecycle.Observer { speechInterpret ->
            val intent = Intent(this, TaskDetailsActivity::class.java)
            val task = TaskModel()
            task.isCompleted = false
            task.note = speechInterpret!!.taskDetails
            task.name = speechInterpret!!.taskDeriveTitle

            if(speechInterpret.taskIsTimeSensitive!!) {
                val dateFormat = if (speechInterpret.dueDateTime!!.length == 10) {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                } else {
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                }
                val date = dateFormat.parse(speechInterpret.dueDateTime!!)
                task.dueDate = date
            }

            task.idCreate()
            intent.putExtra("task", Gson().toJson(task))
            startActivity(intent)
        })

        mainViewModel!!.getTasks(this)


        val filter = IntentFilter()
        filter.addAction(MyConstants.ALARMTRIGGERIDACTION2)
        registerReceiver(alarmBroadcastReceiver, filter)

    }

    private fun showFABMenu() {
        isFABOpen = true
        binding.fabOption1.visibility = View.VISIBLE
        binding.fabOption2.visibility = View.VISIBLE
        binding.fab.animate().rotation(45f)
        binding.fabOption1.animate().translationY(-resources.getDimension(R.dimen.standard_55))
        binding.fabOption2.animate().translationY(-resources.getDimension(R.dimen.standard_105))
    }

    private fun closeFABMenu() {
        isFABOpen = false
        binding.fab.animate().rotation(0f)
        binding.fabOption1.animate().translationY(0f).withEndAction {
            binding.fabOption1.visibility = View.INVISIBLE
        }
        binding.fabOption2.animate().translationY(0f).withEndAction {
            binding.fabOption2.visibility = View.INVISIBLE
        }
    }

    fun updateTitleDate(){
        val calendar = Calendar.getInstance()
        val dateFormatDate = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())

        val dateString = dateFormatDate.format(calendar.time)
        supportActionBar?.title = dateString
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                true
            }
            else -> true
        }
    }

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            // Called when the SpeechRecognizer is ready to start listening
            Log.d("RecognitionListener", "onReadyForSpeech")
            Utils.showMessage(applicationContext,"Listening...")

        }

        override fun onBeginningOfSpeech() {
            // Called when the user starts speaking
            Log.d("RecognitionListener", "onBeginningOfSpeech")
        }

        override fun onRmsChanged(rmsdB: Float) {
            // Called when the input volume changes
            Log.d("RecognitionListener", "onRmsChanged: $rmsdB")
        }

        override fun onBufferReceived(buffer: ByteArray?) {
            // Called when more sound has been captured
            Log.d("RecognitionListener", "onBufferReceived")
        }

        override fun onEndOfSpeech() {
            // Called when the user stops speaking
            Log.d("RecognitionListener", "onEndOfSpeech")
            Utils.showMessage(applicationContext,"Processing...",true)

        }

        override fun onError(error: Int) {
            // Called when an error occurs
            Log.e("RecognitionListener", "onError: $error")
        }

        override fun onResults(results: Bundle?) {
            // Called when recognition results are ready
            results?.let {
                val matches = it.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.let { resultList ->
                    // Process the recognition results, e.g., display them in a TextView or perform a search
                    val recognizedText = resultList[0] // Get the most likely result
                    mainViewModel!!.interpretSpeech(applicationContext,recognizedText)
                }
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            // Called when partial recognition results are available
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
            // Called when a miscellaneous event occurs
        }
    }

    override fun onDestroy() {
        // Release the SpeechRecognizer
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.destroy()
        }
        unregisterReceiver(alarmBroadcastReceiver)

        super.onDestroy()
    }

    private fun startListening() {
        requestMicrophonePermission()

        speechRecognizer.startListening(speechRecognizerIntent)
    }

    private fun requestMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_REQUEST_CODE)
        } else {
            // Microphone permission is already granted, you can proceed with your functionality
        }
    }
    private val RECORD_AUDIO_REQUEST_CODE = 1

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            RECORD_AUDIO_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Microphone permission was granted, you can proceed with your functionality
                } else {
                    // Microphone permission was denied, you should inform the user and disable related features
                }
                return
            }
            else -> {
                // Ignore other request codes
            }
        }
    }



}