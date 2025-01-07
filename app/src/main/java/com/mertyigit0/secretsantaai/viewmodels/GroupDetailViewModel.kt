package com.mertyigit0.secretsantaai.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mertyigit0.secretsantaai.data.model.Group
import com.mertyigit0.secretsantaai.data.model.User
import com.mertyigit0.secretsantaai.data.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    private val groupRepository: GroupRepository // GroupRepository Hilt ile sağlanacak
) : ViewModel() {

    // Group verisini tutan LiveData
    private val _groupDetails = MutableLiveData<Group?>()  // Nullable yapıldı
    val groupDetails: LiveData<Group?> = _groupDetails

    // Draw performed status
    private val _isDrawPerformed = MutableLiveData<Boolean>(false) // Default olarak false
    val isDrawPerformed: LiveData<Boolean> = _isDrawPerformed

    // Grup bilgilerini almak için fonksiyon
    fun getGroupDetails(groupId: String) {
        // Asenkron olarak grup bilgilerini çekiyoruz
        viewModelScope.launch {
            try {
                // Repository'den veriyi çekme
                val group = withContext(Dispatchers.IO) {
                    groupRepository.getGroupDetails(groupId)
                }
                if (group != null) {
                    // Eğer grup verisi null değilse, LiveData'ya atıyoruz
                    _groupDetails.postValue(group)
                   // _error.postValue(null)  // Hata varsa sıfırlıyoruz

                    // Çekiliş yapılmışsa, isDrawPerformed'i true yapıyoruz
                    _isDrawPerformed.postValue(group.drawResults != null)
                } else {
                    // Grup bulunamazsa, hata mesajı gönderiyoruz
                    //_error.postValue("Group not found.")
                }
            } catch (e: Exception) {
                // Hata durumunu yakalayıp, hata mesajını LiveData'ya gönderiyoruz
                _groupDetails.postValue(null)
                //_error.postValue("Failed to load group details: ${e.message}")
            }
        }
    }

    // Çekilişi yapmak için bir fonksiyon (çekiliş sonucu Firestore'a kaydedilecek)
    fun performDraw(groupId: String, users: List<User>) {
        val userIds = users.map { it.userId }
        val shuffledUserIds = userIds.shuffled()

        // Çekilişi yap
        val drawResults = mutableMapOf<String, String>()
        for (i in userIds.indices) {
            val giverId = userIds[i]
            val recipientId = shuffledUserIds[(i + 1) % userIds.size]
            drawResults[giverId] = recipientId
        }

        // Çekiliş sonuçlarını Firestore'a kaydet
        FirebaseFirestore.getInstance().collection("groups")
            .document(groupId)
            .update("drawResults", drawResults)
            .addOnSuccessListener {
                // Çekiliş tamamlandıktan sonra durumu güncelle
                _isDrawPerformed.postValue(true)
            }
    }
}

