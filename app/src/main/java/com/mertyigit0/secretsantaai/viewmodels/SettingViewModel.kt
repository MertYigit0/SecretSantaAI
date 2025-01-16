package com.mertyigit0.secretsantaai.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.secretsantaai.data.repository.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    // Language flow
    val languageFlow: Flow<String> = dataStoreManager.languageFlow

    fun saveLanguagePreference(languageCode: String) {
        viewModelScope.launch {
            dataStoreManager.saveLanguage(languageCode)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun isUserLoggedOut(): Boolean {
        return auth.currentUser == null
    }
}
