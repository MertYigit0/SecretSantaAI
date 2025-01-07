package com.mertyigit0.secretsantaai.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.mertyigit0.secretsantaai.BuildConfig
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
        apiKey = BuildConfig.MAPS_API_KEY// API anahtarınızı buraya ekleyin
    )

    // Kullanıcının gönderdiği bilgilerle hediye önerisi alıyoruz
    fun getGiftRecommendation(age: String, gender: String, occasion: String) {
        _uiState.value = UiState.Loading


        // API'ye gönderilecek prompt
        val prompt = "Öneri al: Yaş: $age, Cinsiyet: $gender, Özel Gün: $occasion"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        text(prompt)  // API'ye metin isteği gönder
                    }
                )

                // Gelen yanıtı kontrol et
                response.text?.let { outputContent ->
                    _uiState.value = UiState.Success(outputContent)  // Başarılı sonuç
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Bir hata oluştu")  // Hata durumu
            }
        }
    }
}
