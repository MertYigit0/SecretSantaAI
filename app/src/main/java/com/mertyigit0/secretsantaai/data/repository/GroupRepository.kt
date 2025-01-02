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

        // Tüm grupları al
        val groupDocs = db.collection("groups")
            .get()
            .await()

        for (document in groupDocs) {
            val group = document.toObject(Group::class.java)

            // Üyeler listesini kontrol et (userId'yi bulmak için)
            val isMember = group.members.any { member ->
                // Üyelerin her biri, bir Map<String, String> olmalı
                val memberMap = member as? Map<String, String>
                memberMap?.get("userId") == userId  // Kullanıcının userId'si burada kontrol ediliyor
            }

            if (isMember) {
                groups.add(group)
            }
        }

        return groups
    }




}
