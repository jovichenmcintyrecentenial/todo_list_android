package com.centennial.team_15_mapd_721_todo_app.models

import com.google.gson.annotations.SerializedName

data class SpeechInterpret (
    @SerializedName("task_derive_title") val taskDeriveTitle: String? = null,
    @SerializedName("task_details") val taskDetails: String? = null,
    @SerializedName("task_is_time_sensitive") val taskIsTimeSensitive: Boolean? = null,
    @SerializedName("due_date_time") val dueDateTime: String? = null
)