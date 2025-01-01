package com.mertyigit0.secretsantaai.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mertyigit0.secretsantaai.data.model.Group
import com.mertyigit0.secretsantaai.data.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val groupRepository: GroupRepository, // Repository inject ediliyor
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _groups = MutableLiveData<List<Group>>()
    val groups: LiveData<List<Group>> get() = _groups

    init {
        fetchUserGroups()
    }

    private fun fetchUserGroups() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        // Kullanıcının gruplarını almak için repository kullanılıyor
        viewModelScope.launch {
            try {
                val groupsJoined = groupRepository.getUserGroups(userId)
                if (groupsJoined.isNotEmpty()) {
                    _groups.value = groupsJoined
                } else {
                    _groups.value = emptyList()
                }
            } catch (e: Exception) {
                _groups.value = emptyList()
            }
        }
    }
}



