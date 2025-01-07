package com.mertyigit0.secretsantaai.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mertyigit0.secretsantaai.data.model.Group
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

    // Hata durumunu tutacak LiveData
    private val _error = MutableLiveData<String?>()  // Hata mesajlarını tutacak
    val error: LiveData<String?> = _error

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
                    _error.postValue(null)  // Hata varsa sıfırlıyoruz
                } else {
                    // Grup bulunamazsa, hata mesajı gönderiyoruz
                    _error.postValue("Group not found.")
                }
            } catch (e: Exception) {
                // Hata durumunu yakalayıp, hata mesajını LiveData'ya gönderiyoruz
                _groupDetails.postValue(null)
                _error.postValue("Failed to load group details: ${e.message}")
            }
        }
    }
}
