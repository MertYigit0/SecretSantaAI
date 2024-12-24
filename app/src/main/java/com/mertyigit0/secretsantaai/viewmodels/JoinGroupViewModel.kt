
package com.mertyigit0.secretsantaai.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mertyigit0.secretsantaai.data.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinGroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    // Grubun var olup olmadığını kontrol etme
    fun checkIfGroupExists(groupId: String): LiveData<Result<Boolean>> {
        return groupRepository.checkIfGroupExists(groupId)
    }

    // Kullanıcıyı gruba eklemek
    fun joinGroup(groupId: String, userId: String): LiveData<Result<Boolean>> {
        return groupRepository.joinGroup(groupId, userId)
    }
}
