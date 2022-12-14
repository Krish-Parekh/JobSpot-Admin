package com.example.jobspotadmin.home.fragment.jobsFragment

import android.net.Uri
import androidx.lifecycle.ViewModel

class JobsViewModel : ViewModel() {

    private var imageUri: Uri? = null

    fun setImageUri(imageUri: Uri?) {
        this@JobsViewModel.imageUri = imageUri
    }

    fun getImageUri(): Uri? {
        return this@JobsViewModel.imageUri
    }
}