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
        apiKey = BuildConfig.MAPS_API_KEY
    )

    // Kullanıcının gönderdiği bilgilerle hediye önerisi alıyoruz
    fun getGiftRecommendation(age: String, gender: String, occasion: String, interests: String, budget: String) {
        _uiState.value = UiState.Loading

        // API'ye gönderilecek prompt
        val prompt = """
    Yaş: $age
    Cinsiyet: $gender
    Özel Gün: $occasion
    İlgi Alanları: $interests
    Bütçe: $budget

    Lütfen aşağıdaki formatta tam olarak 10 adet hediye önerisi sunun. Her öneri kısa ve öz bir cümle olmalı ve sadece hediye önerisi içermelidir. Başka hiçbir açıklama veya metin eklemeyin.
    
    1. [Hediye Önerisi 1]
    2. [Hediye Önerisi 2]
    3. [Hediye Önerisi 3]
    4. [Hediye Önerisi 4]
    5. [Hediye Önerisi 5]
    6. [Hediye Önerisi 6]
    7. [Hediye Önerisi 7]
    8. [Hediye Önerisi 8]
    9. [Hediye Önerisi 9]
    10. [Hediye Önerisi 10]
""".trimIndent()

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
