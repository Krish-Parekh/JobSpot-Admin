package com.example.jobspotadmin.auth.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    private var imageUri : Uri? = null;

    fun setImageUri(imageUri : Uri){
        this.imageUri = imageUri
    }

    fun getImageUri() : Uri? {
        return this.imageUri
    }
}