package com.example.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.mertyigit0.secretsantaai.data.model.Group
import com.mertyigit0.secretsantaai.data.model.User
import java.util.*

class CreateGroupViewModel : ViewModel() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun createGroup(email: String,name: String, groupName: String, note: String, budget: Int, selectedDate: String?) {
        val userId = firebaseAuth.currentUser?.uid ?: return

        // Grup verisini oluşturuyoruz
        val groupId = UUID.randomUUID().toString()

        // Grup üyeleri listesini oluşturuyoruz, başta sadece oluşturucu üye
        val users = listOf(
            User(
                userId = userId,
                email =  email,
                username = name,
            )
        )

        // Grup verisini, Group veri yapısına uygun olarak oluşturuyoruz
        val groupData = Group(
            groupId = groupId,
            groupName = groupName,
            users = users, // Üyeleri güncelledik
            createdAt = Calendar.getInstance().time.toString(),
            budget = budget,
            note = note,
            date = selectedDate // 'date' olarak güncellendi
        )


        // Firestore'a grup verisini kaydediyoruz
        firestore.collection("groups").document(groupId).set(groupData)
            .addOnSuccessListener {
                // Kullanıcıyı güncelliyoruz
                firestore.collection("users").document(userId).update(
                    "groupsCreated", FieldValue.arrayUnion(groupId),
                    "groupsJoined", FieldValue.arrayUnion(groupId) // Kullanıcının gruplarına katılmayı da ekliyoruz
                )
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
