package com.mertyigit0.secretsantaai.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _loginStatus = MutableLiveData<Result<Unit>>()
    val loginStatus: LiveData<Result<Unit>> get() = _loginStatus

    fun login(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginStatus.postValue(Result.success(Unit))
                } else {
                    _loginStatus.postValue(Result.failure(task.exception ?: Exception("Login failed")))
                }
            }
    }
}
