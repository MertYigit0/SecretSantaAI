package com.mertyigit0.secretsantaai.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.mertyigit0.secretsantaai.data.model.Group
import javax.inject.Inject

class GroupRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {

    // Yeni grup oluşturma fonksiyonu
    fun createGroup(groupName: String, userId: String): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()

        // Yeni grup için benzersiz ID oluştur
        val groupId = generateUniqueId(5)

        firestore.collection("groups").document(groupId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result.exists()) {
                    result.value = Result.failure(Exception("Group already exists"))
                } else {
                    // Grup oluşturuluyor
                    val group = hashMapOf(
                        "groupId" to groupId,
                        "groupName" to groupName,
                        "creatorId" to userId, // Grubu oluşturan kişinin userId'si
                        "members" to listOf(userId) // Başlangıçta grup yaratan kişi
                    )

                    firestore.collection("groups").document(groupId)
                        .set(group)
                        .addOnCompleteListener { createTask ->
                            result.value = if (createTask.isSuccessful) {
                                // Gruba kullanıcıyı ekle
                                addUserToGroup(userId, groupId)
                                Result.success(groupId)
                            } else {
                                Result.failure(createTask.exception ?: Exception("Group creation failed"))
                            }
                        }
                }
            }

        return result
    }

    // Gruba katılma fonksiyonu
    fun joinGroup(groupId: String, userId: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()

        firestore.collection("groups").document(groupId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result.exists()) {
                    // Grubu bulduktan sonra, üyeyi ekle
                    firestore.collection("groups").document(groupId)
                        .update("members", FieldValue.arrayUnion(userId))
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                // Kullanıcıyı gruba ekle
                                result.value = Result.success(true)
                            } else {
                                result.value = Result.failure(Exception("Failed to update group members"))
                            }
                        }
                } else {
                    result.value = Result.failure(Exception("Group not found"))
                }
            }

        return result
    }

    // Kullanıcıyı gruba eklemek için yardımcı fonksiyon
    private fun addUserToGroup(userId: String, groupId: String) {
        val userDoc = firestore.collection("users").document(userId)
        userDoc.get().addOnSuccessListener { document ->
            if (document.exists()) {
                userDoc.update("groupIds", FieldValue.arrayUnion(groupId))
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            task.exception?.printStackTrace()
                        }
                    }
            } else {
                userDoc.set(mapOf("groupIds" to listOf(groupId)), SetOptions.merge())
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            task.exception?.printStackTrace()
                        }
                    }
            }
        }
    }


    // Benzersiz ID oluşturma fonksiyonu
    private fun generateUniqueId(length: Int): String {
        val chars = "0123456789"
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }



    fun getUserGroups(): LiveData<Result<List<Group>>> {
        val result = MutableLiveData<Result<List<Group>>>()
        val userId = firebaseAuth.currentUser?.uid

        if (userId == null) {
            result.value = Result.failure(Exception("User not logged in"))
            return result
        }

        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                val groupIds = document["groupIds"] as? List<String> ?: emptyList()
                if (groupIds.isEmpty()) {
                    result.value = Result.success(emptyList())
                } else {
                    firestore.collection("groups")
                        .whereIn("groupId", groupIds)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            val groups = querySnapshot.documents.mapNotNull { it.toObject(Group::class.java) }
                            result.value = Result.success(groups)
                        }
                        .addOnFailureListener { exception ->
                            result.value = Result.failure(exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                result.value = Result.failure(exception)
            }

        return result
    }
}

