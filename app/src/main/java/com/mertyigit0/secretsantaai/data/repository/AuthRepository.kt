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

        // Firebase Authentication ile kullanıcı kaydı
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Kullanıcı başarıyla kaydedildi
                    val currentUser = firebaseAuth.currentUser
                    if (currentUser != null) {
                        // Kullanıcıya ait unique userId varsa kullan, yoksa oluştur
                        val userId = generateUniqueId(6)

                        val user = hashMapOf(
                            "email" to email,
                            "userId" to userId, // Yeni kullanıcı için bir userId oluşturulacak
                            "groupIds" to listOf<String>() // Başlangıçta grup ID'leri boş bir liste
                        )
                        // Firestore'da kullanıcı bilgilerini kaydet
                        firestore.collection("users").document(currentUser.uid)
                            .set(user)
                            .addOnCompleteListener { firestoreTask ->
                                result.value = if (firestoreTask.isSuccessful) {
                                    Result.success(true)
                                } else {
                                    Result.failure(firestoreTask.exception ?: Exception("Firestore error"))
                                }
                            }
                    }
                } else {
                    result.value = Result.failure(task.exception ?: Exception("Registration failed"))
                }
            }
        return result
    }

    // Kullanıcı ID'sini almak için fonksiyon
    fun getUserId(): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            // Kullanıcı zaten giriş yaptıysa, Firestore'dan kullanıcı ID'sini al
            firestore.collection("users").document(currentUser.uid)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = task.result?.getString("userId") // Firestore'dan kullanıcı ID'si alınır
                        if (userId != null) {
                            result.value = Result.success(userId)
                        } else {
                            result.value = Result.failure(Exception("User ID not found"))
                        }
                    } else {
                        result.value = Result.failure(task.exception ?: Exception("Failed to get user ID"))
                    }
                }
        } else {
            result.value = Result.failure(Exception("User not logged in"))
        }

        return result
    }

    // Kullanıcı giriş işlemi
    fun loginUser(email: String, password: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.value = Result.success(true)
                } else {
                    result.value = Result.failure(task.exception ?: Exception("Login failed"))
                }
            }
        return result
    }

    // Kullanıcıya ait unique bir ID oluşturma fonksiyonu
    private fun generateUniqueId(length: Int): String {
        val chars = "0123456789"
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }
}


