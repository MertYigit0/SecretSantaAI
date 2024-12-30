package com.mertyigit0.secretsantaai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mertyigit0.secretsantaai.data.model.Group
import com.mertyigit0.secretsantaai.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GroupRepository @Inject constructor(
    private val db: FirebaseFirestore // FirebaseFirestore Hilt ile sağlanacak
) {

    // Grupların bilgilerini almak
    suspend fun getGroupDetails(groupId: String): Group? {
        val groupDoc = db.collection("groups").document(groupId).get().await()
        return if (groupDoc.exists()) {
            groupDoc.toObject(Group::class.java)
        } else {
            null
        }
    }

    // Kullanıcı bilgilerini almak
    suspend fun getUserDetails(userId: String): User? {
        val userDoc = db.collection("users").document(userId).get().await()
        return if (userDoc.exists()) {
            userDoc.toObject(User::class.java)
        } else {
            null
        }
    }
}
