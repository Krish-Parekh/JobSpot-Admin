package com.example.jobspotadmin.util

data class UiState(
    var loading : Boolean = false,
    var success : Boolean = false,
    val failed : Boolean = false,
)