package com.mertyigit0.secretsantaai.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class GroupRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    fun createGroup(groupName: String, creatorId: String): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()
        val groupId = generateUniqueId(5)
        val group = hashMapOf(
            "groupId" to groupId,
            "groupName" to groupName,
            "creatorId" to creatorId,
            "members" to listOf(creatorId)
        )

        firestore.collection("groups").document(groupId)
            .set(group)
            .addOnCompleteListener { task ->
                result.value = if (task.isSuccessful) {
                    Result.success(groupId)
                } else {
                    Result.failure(task.exception ?: Exception("Group creation failed"))
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

    // Grubun var olup olmadığını kontrol eden fonksiyon
    fun checkIfGroupExists(groupId: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()

        firestore.collection("groups").document(groupId)
            .get()
            .addOnCompleteListener { task ->
                result.value = if (task.isSuccessful && task.result.exists()) {
                    Result.success(true)  // Grup mevcut
                } else {
                    Result.success(false)  // Grup mevcut değil
                }
            }
            .addOnFailureListener {
                result.value = Result.failure(it)  // Hata durumunda
            }

        return result
    }

    // Kullanıcıyı gruba eklemek için fonksiyon
    fun joinGroup(groupId: String, userId: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()

        firestore.collection("groups").document(groupId)
            .update("members", listOf(userId))  // Grup üyelerine kullanıcıyı ekleme
            .addOnCompleteListener { task ->
                result.value = if (task.isSuccessful) {
                    Result.success(true)  // Başarılı
                } else {
                    Result.failure(task.exception ?: Exception("Group join failed"))
                }
            }

        return result
    }



}
