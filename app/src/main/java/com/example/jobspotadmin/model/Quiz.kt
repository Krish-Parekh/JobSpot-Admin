package com.example.jobspotadmin.model

data class Quiz(
    var uid: String = "",
    var title: String = "",
    var duration: String = "",
    var question: List<Question> = emptyList()
)
