package com.mertyigit0.secretsantaai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mertyigit0.secretsantaai.data.model.Group
import com.mertyigit0.secretsantaai.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GroupRepository @Inject constructor(
    private val db: FirebaseFirestore
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

    // Kullanıcının katıldığı grupları almak
    suspend fun getUserGroups(userId: String): List<Group> {
        val groups = mutableListOf<Group>()

        // Tüm grupları al
        val groupDocs = db.collection("groups").get().await()

        for (document in groupDocs) {
            val group = document.toObject(Group::class.java)

            // Kullanıcının userId'sini kontrol et
            val isMember = group.users.any { user ->
                user.userId == userId  // Kullanıcının userId'si burada kontrol ediliyor
            }

            if (isMember) {
                groups.add(group)
            }
        }

        return groups
    }
}
