package com.mertyigit0.secretsantaai.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.mertyigit0.secretsantaai.data.model.User
import kotlin.random.Random

class DrawResultViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    // Çekiliş sonuçlarını tutacak bir canlı veri
    private val _santaAssignments = MutableLiveData<Map<User, User>>()
    val santaAssignments: LiveData<Map<User, User>> get() = _santaAssignments

    // Çekiliş işlemi
    fun performDraw(users: List<User>, groupId: String) {
        val userIds = users.map { it.userId }
        val shuffledUserIds = userIds.shuffled()

        // Çekilişi yap
        val assignments = mutableMapOf<User, User>()
        for (i in userIds.indices) {
            val giver = users.find { it.userId == userIds[i] }!!
            val recipient = users.find { it.userId == shuffledUserIds[(i + 1) % userIds.size] }!!

            // Çekiliş sonuçlarını kaydediyoruz
            assignments[giver] = recipient

            // Sonuçları Firestore'a kaydet
            firestore.collection("drawResults")
                .document(groupId)
                .collection("assignments")
                .document(giver.userId)
                .set(mapOf("recipientId" to recipient.userId))
        }

        // Sonuçları LiveData'ya ekleyerek UI'yı güncelliyoruz
        _santaAssignments.value = assignments
    }

    // Kullanıcıya hediye alacağı kişiyi döndüren fonksiyon
    fun getGiftReceiverForUser(user: User): User? {
        return _santaAssignments.value?.get(user)
    }
}
