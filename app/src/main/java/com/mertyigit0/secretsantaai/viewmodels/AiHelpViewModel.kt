package com.mertyigit0.secretsantaai.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.mertyigit0.secretsantaai.BuildConfig
import com.mertyigit0.secretsantaai.R
import com.mertyigit0.secretsantaai.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiHelpViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.MAPS_API_KEY
    )

    // Kullanıcının gönderdiği bilgilerle hediye önerisi alıyoruz
    fun getGiftRecommendation(
        age: String,
        gender: String,
        occasion: String,
        interests: String,
        budget: String,
        context: Context // Pass context to access resources
    ) {
        _uiState.value = UiState.Loading

        // Get the localized prompt
        val prompt = context.getString(
            R.string.gift_prompt,
            age, gender, occasion, interests, budget
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        text(prompt) // Send the localized prompt to the API
                    }
                )

                response.text?.let { outputContent ->
                    _uiState.value = UiState.Success(outputContent)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "An error occurred")
            }
        }
    }
}
