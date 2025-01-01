package com.mertyigit0.secretsantaai.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mertyigit0.secretsantaai.data.model.Group
import com.mertyigit0.secretsantaai.data.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InviteViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _userGroups = MutableLiveData<List<Group>>()
    val userGroups: LiveData<List<Group>> get() = _userGroups

    // Kullanıcının gruplarını yüklemek için kullanılacak fonksiyon
    fun loadUserGroups(userId: String) {
        // Kullanıcının gruplarını Firebase'den çekiyoruz (grupların listesini almak için)
        viewModelScope.launch {
            try {
                val groups = fetchUserGroups(userId)
                _userGroups.value = groups
            } catch (e: Exception) {
                _userGroups.value = emptyList()
            }
        }
    }

    // Firebase'den kullanıcı gruplarını çeken fonksiyon
    private suspend fun fetchUserGroups(userId: String): List<Group> {
        // Burada, kullanıcının gruplarını Firebase'den alacağız
        // Kullanıcı ID'siyle gruplar alınır
        return groupRepository.getUserGroups(userId)
    }
}
