package com.example.jobspotadmin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Job(
    var authorUid: String = "",
    var imageUri: String = "",
    var title: String = "",
    var name: String = "",
    var city: String = "",
    var salary: String = "",
    var description: String = "",
    var responsibilities: String = "",
    var skillSet: List<String> = emptyList(),
    var uid: String = System.currentTimeMillis().toString(),
) : Parcelable
