package com.mertyigit0.secretsantaai.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mertyigit0.secretsantaai.data.repository.AuthRepository
import com.mertyigit0.secretsantaai.data.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class JoinGroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _joinGroupResult = MutableLiveData<Result<Boolean>>()
    val joinGroupResult: LiveData<Result<Boolean>> get() = _joinGroupResult

    fun joinGroup(groupId: String) {
        val userIdLiveData = authRepository.getUserId()

        userIdLiveData.observeForever { result ->
            result.onSuccess { userId ->
                groupRepository.joinGroup(groupId, userId).observeForever {
                    _joinGroupResult.value = it
                }
            }
            result.onFailure {
                _joinGroupResult.value = Result.failure(it)
            }
        }
    }
}
