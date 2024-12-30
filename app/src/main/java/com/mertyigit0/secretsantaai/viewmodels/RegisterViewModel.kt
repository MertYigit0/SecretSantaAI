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

    fun register(email: String, password: String) {
        _registerStatus.value = Result.success(Unit) // Başlangıç durumu

        viewModelScope.launch {
            try {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Kullanıcı başarıyla oluşturuldu
                            val userId = firebaseAuth.currentUser?.uid
                            if (userId != null) {
                                addUserToFirestore(userId, email)
                            } else {
                                _registerStatus.value = Result.failure(Exception("User ID is null"))
                            }
                        } else {
                            // Kayıt başarısız
                            _registerStatus.value = Result.failure(task.exception ?: Exception("Unknown error"))
                        }
                    }
            } catch (e: Exception) {
                _registerStatus.value = Result.failure(e)
            }
        }
    }

    private fun addUserToFirestore(userId: String, email: String) {
        val user = mapOf(
            "userId" to userId,
            "email" to email,
            "groupsCreated" to emptyList<String>(),
            "groupsJoined" to emptyList<String>()
        )

        firestore.collection("users")
            .document(userId)
            .set(user)
            .addOnSuccessListener {
                _registerStatus.value = Result.success(Unit)
            }
            .addOnFailureListener { exception ->
                _registerStatus.value = Result.failure(exception)
            }
    }
}
