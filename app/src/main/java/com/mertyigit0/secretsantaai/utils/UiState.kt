package com.mertyigit0.secretsantaai.utils

sealed class UiState {
    object Initial : UiState()
    object Loading : UiState()
    data class Success(val recommendation: String) : UiState()
    data class Error(val message: String) : UiState()
}
