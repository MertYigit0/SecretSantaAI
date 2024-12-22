package com.mertyigit0.secretsantaai.utils

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String?, val exception: Exception? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}


enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}
