package com.example.jobspotadmin.model

import com.google.firebase.Timestamp
import java.util.UUID

data class BroadcastNotification(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val body: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val type : String = "BROADCAST"
)

data class HostNotification(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val body: String = "",
    val hostId: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val type : String = "HOST"
)

