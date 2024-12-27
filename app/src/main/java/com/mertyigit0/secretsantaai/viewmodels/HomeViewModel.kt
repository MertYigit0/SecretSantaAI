package com.mertyigit0.secretsantaai.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mertyigit0.secretsantaai.data.model.Group
import com.mertyigit0.secretsantaai.data.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _groups = MutableLiveData<Result<List<Group>>>()
    val groups: LiveData<Result<List<Group>>> get() = _groups

    fun loadUserGroups() {
        groupRepository.getUserGroups().observeForever { result ->
            _groups.value = result
        }
    }
}
