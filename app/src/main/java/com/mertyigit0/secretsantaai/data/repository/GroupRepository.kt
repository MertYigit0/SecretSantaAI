package com.mertyigit0.secretsantaai.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import javax.inject.Inject

class GroupRepository @Inject constructor(
    private val firestore: FirebaseFirestore
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
        userDoc.update("groupIds", FieldValue.arrayUnion(groupId))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Kullanıcıya grup ID'si başarıyla eklendi
                } else {
                    // Hata işlemi
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
}

