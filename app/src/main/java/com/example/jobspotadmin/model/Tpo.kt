package com.example.jobspotadmin.model

import android.os.Parcelable
import com.example.jobspotadmin.util.Constants.Companion.ROLE_TYPE_TPO
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tpo(
    var uid : String = "",
    var email : String = "",
    var username : String = "",
    var mobile : String = "",
    var dob : String = "",
    var gender : String = "",
    var imageUri : String = "",
    var stream : String = "",
    var qualification : String = "",
    var experience : String = "",
    var biography : String = "",
    val roleType : String = ROLE_TYPE_TPO
): Parcelable