package com.example.jobspotadmin.auth

import android.animation.ObjectAnimator
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.TextView
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.ActivityAuthBinding
import com.example.jobspotadmin.util.LoadingDialog

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}