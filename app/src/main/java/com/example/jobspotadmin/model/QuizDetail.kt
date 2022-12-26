package com.example.jobspotadmin.model

data class QuizDetail(
    var quizId : String = "",
    var studentCount: String = "0",
    var quizName: String = "",
    var studentIds: List<String> = emptyList()
)
