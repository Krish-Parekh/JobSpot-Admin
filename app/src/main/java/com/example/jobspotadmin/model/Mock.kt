package com.example.jobspotadmin.model

data class Mock(
    var uid: String = System.currentTimeMillis().toString(),
    var title: String = "",
    var duration: String = "",
    var mockQuestion: List<MockQuestion> = emptyList()
)
