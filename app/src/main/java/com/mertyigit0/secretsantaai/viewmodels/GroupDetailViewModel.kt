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

    private val _groupDetails = MutableLiveData<Group?>()  // Nullable yapıldı
    val groupDetails: LiveData<Group?> = _groupDetails

    fun getGroupDetails(groupId: String) {
        // Asenkron olarak grup bilgilerini çekiyoruz
        viewModelScope.launch {
            try {
                val group = withContext(Dispatchers.IO) {
                    groupRepository.getGroupDetails(groupId)
                }
                _groupDetails.postValue(group)
            } catch (e: Exception) {
                // Hata durumunu ele alabiliriz
            }
        }
    }
}
