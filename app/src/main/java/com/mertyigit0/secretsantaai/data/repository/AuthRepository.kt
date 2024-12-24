package com.mertyigit0.secretsantaai.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    fun registerUser(email: String, password: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = generateUniqueId(6)
                    val user = hashMapOf(
                        "email" to email,
                        "userId" to userId
                    )
                    // Firestore'da kullanıcı bilgilerini kaydet
                    firestore.collection("users").document(firebaseAuth.currentUser!!.uid)
                        .set(user)
                        .addOnCompleteListener { firestoreTask ->
                            result.value = if (firestoreTask.isSuccessful) {
                                Result.success(true)
                            } else {
                                Result.failure(firestoreTask.exception ?: Exception("Firestore error"))
                            }
                        }
                } else {
                    result.value = Result.failure(task.exception ?: Exception("Registration failed"))
                }
            }
        return result
    }

    private fun generateUniqueId(length: Int): String {
        val chars = "0123456789"
        return (1..length)
            .map { chars.random() }
            .joinToString("")
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


