package com.mertyigit0.secretsantaai.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.secretsantaai.data.repository.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    auth: FirebaseAuth,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _languageCode = MutableLiveData<String>()
    val languageCode: LiveData<String> get() = _languageCode

    private val _isUserAuthenticated = MutableLiveData<Boolean>()
    val isUserAuthenticated: LiveData<Boolean> get() = _isUserAuthenticated

    init {
        // Kullanıcının oturum durumunu kontrol et
        _isUserAuthenticated.value = auth.currentUser != null

        // Dil değişikliklerini gözlemle
        viewModelScope.launch {
            dataStoreManager.languageFlow.collect { languageCode ->
                _languageCode.postValue(languageCode)
            }
        }
    }
}
