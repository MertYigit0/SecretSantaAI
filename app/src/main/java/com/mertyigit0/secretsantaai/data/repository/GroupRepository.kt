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
}
