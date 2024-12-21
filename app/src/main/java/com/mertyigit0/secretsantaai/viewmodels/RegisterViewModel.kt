package com.mertyigit0.secretsantaai.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mertyigit0.secretsantaai.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _registerStatus = MutableLiveData<Result<Boolean>>()
    val registerStatus: LiveData<Result<Boolean>> = _registerStatus

    fun register(email: String, password: String) {
        authRepository.registerUser(email, password).observeForever { result ->
            _registerStatus.value = result
        }
    }
}
