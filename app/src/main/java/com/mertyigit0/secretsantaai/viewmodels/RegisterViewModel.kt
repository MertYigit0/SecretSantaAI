package com.mertyigit0.secretsantaai.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _registerStatus = MutableLiveData<Result<Unit>>()
    val registerStatus: LiveData<Result<Unit>> get() = _registerStatus

    fun register(email: String, password: String, username: String) {
        viewModelScope.launch {
            // Kullanıcı adı kontrolü yapılacak
            checkUsernameAvailability(username) { isAvailable ->
                if (isAvailable) {
                    // Kullanıcı adı uygun, Firebase Auth ile kullanıcı oluşturuluyor
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = firebaseAuth.currentUser?.uid
                                if (userId != null) {
                                    addUserToFirestore(userId, email, username)
                                } else {
                                    _registerStatus.value = Result.failure(Exception("User ID is null"))
                                }
                            } else {
                                // Firebase Auth işlemi başarısız oldu
                                _registerStatus.value = Result.failure(task.exception ?: Exception("Unknown error"))
                            }
                        }
                } else {
                    // Kullanıcı adı zaten alınmışsa hata mesajı
                    _registerStatus.value = Result.failure(Exception("Username already taken"))
                }
            }
        }
    }



    private fun checkUsernameAvailability(username: String, onResult: (Boolean) -> Unit) {
        firestore.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                onResult(documents.isEmpty()) // Eğer döküman yoksa kullanıcı adı uygundur
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    private fun addUserToFirestore(userId: String, email: String, username: String) {
        val user = mapOf(
            "userId" to userId,
            "email" to email,
            "username" to username,
            "groupsCreated" to emptyList<String>(),
            "groupsJoined" to emptyList<String>()
        )

        firestore.collection("users")
            .document(userId)
            .set(user)
            .addOnSuccessListener {
                // Firestore veritabanına kullanıcı kaydedildiğinde başarılı sonuç
                _registerStatus.value = Result.success(Unit)
            }
            .addOnFailureListener { exception ->
                // Veritabanı kaydetme hatası durumunda hata mesajı
                _registerStatus.value = Result.failure(exception)
            }
    }

}
