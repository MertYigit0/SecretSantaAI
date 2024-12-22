package com.mertyigit0.secretsantaai.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    fun registerUser(email: String, password: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                result.value = if (task.isSuccessful) {
                    Result.success(true)
                } else {
                    Result.failure(task.exception ?: Exception("Registration failed"))
                }
            }
        return result
    }

    fun loginUser(email: String, password: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                result.value = if (task.isSuccessful) {
                    Result.success(true)
                } else {
                    Result.failure(task.exception ?: Exception("Login failed"))
                }
            }
        return result
    }
}
