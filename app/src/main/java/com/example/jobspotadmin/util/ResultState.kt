package com.example.jobspotadmin.util
/*
* Used as a wrapper for the remote result
* */
sealed class ResultState<out T> {
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Failure(val error: Exception) : ResultState<Nothing>()
    object Loading : ResultState<Nothing>()
}
