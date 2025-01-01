package com.example.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import java.util.*

class CreateGroupViewModel : ViewModel() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun createGroup(userName: String, groupName: String, note: String, budget: Int, selectedDate: String?) {
        val userId = firebaseAuth.currentUser?.uid ?: return

        // Grup verisini oluşturuyoruz
        val groupId = UUID.randomUUID().toString()
        val groupData = hashMapOf(
            "groupId" to groupId,
            "groupName" to groupName,
            "createdBy" to userId,
            "members" to listOf(userId), // Başlangıçta sadece kullanıcı gruba dahil
            "createdAt" to Calendar.getInstance().time.toString(),
            "budget" to budget,
            "note" to note,
            "date" to selectedDate // 'date' olarak güncellendi
        )

        // Kullanıcı verisini oluşturuyoruz (kullanıcının gruplarına eklemek için)
        val userData = hashMapOf(
            "userId" to userId,
            "name" to userName,
            "email" to firebaseAuth.currentUser?.email,
            "groupsCreated" to listOf(groupId),
            "groupsJoined" to listOf<String>() // Şu an sadece oluşturduğu grup var
        )

        // Firestore'a grup verisini kaydediyoruz
        firestore.collection("groups").document(groupId).set(groupData)
            .addOnSuccessListener {
                // Kullanıcıyı güncelliyoruz
                firestore.collection("users").document(userId).update("groupsCreated", FieldValue.arrayUnion(groupId))
                    .addOnSuccessListener {
                        // Grup oluşturuldu ve kullanıcı güncellendi
                    }
            }
            .addOnFailureListener { e ->
                // Hata durumunda log yazdırıyoruz
                e.printStackTrace()
            }
    }
}

