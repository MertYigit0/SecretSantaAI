package com.mertyigit0.secretsantaai.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class DrawResultViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    // Kullanıcıya hediye alacağı kişiyi döndüren fonksiyon
    fun fetchDrawResult(userId: String, groupId: String) {
        firestore.collection("groups")
            .document(groupId)
            .get()
            .addOnSuccessListener { document ->
                val drawResults = document.get("drawResults") as? Map<String, String>
                val recipientId = drawResults?.get(userId)

                // Alınan kullanıcı adını getir
                if (recipientId != null) {
                    fetchRecipientName(recipientId)
                }
            }
    }

    private fun fetchRecipientName(recipientId: String) {
        firestore.collection("users")
            .document(recipientId)
            .get()
            .addOnSuccessListener { document ->
                val recipientName = document.getString("username")
                _giftReceiver.postValue(recipientName)
            }
    }

    private val _giftReceiver = MutableLiveData<String?>()
    val giftReceiver: MutableLiveData<String?> get() = _giftReceiver
}
