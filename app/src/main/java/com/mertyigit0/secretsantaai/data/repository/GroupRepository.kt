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

    // Kullanıcının katıldığı grupları almak
    suspend fun getUserGroups(userId: String): List<Group> {
        val groups = mutableListOf<Group>()
        val groupDocs = db.collection("groups")
            .whereArrayContains("members", userId) // "members" listesinde userId'yi arar
            .get()
            .await()

        for (document in groupDocs) {
            val group = document.toObject(Group::class.java)
            groups.add(group)
        }

        return groups
    }
}
