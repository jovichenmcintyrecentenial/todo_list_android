package com.centennial.team_15_mapd_721_todo_app.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class ApiClient {
    companion object {
        private const val BASE_URL = "https://todo.jamaica-translate.workers.dev"

        fun interpretTaskInfo(context: Context, taskInfo: String, currentDateTime: String, responseListener: Response.Listener<String>, errorListener: Response.ErrorListener) {
            val url = "$BASE_URL/interpret"
            val requestQueue = Volley.newRequestQueue(context.applicationContext)
            val request = object : StringRequest(Request.Method.POST, url, responseListener, errorListener) {
                override fun getBody(): ByteArray {
                    val body = "{\"task_info\":\"$taskInfo\",\"current_date_time\":\"$currentDateTime\"}"
                    return body.toByteArray()
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }
            requestQueue.add(request)
        }
    }
}