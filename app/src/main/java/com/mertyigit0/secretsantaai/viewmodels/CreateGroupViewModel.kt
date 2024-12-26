package com.mertyigit0.secretsantaai.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mertyigit0.secretsantaai.data.repository.AuthRepository
import com.mertyigit0.secretsantaai.data.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class CreateGroupViewModel  @Inject constructor(
    private val authRepository: AuthRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {

    // Kullanıcı ID'sini almak için fonksiyon
    fun getUserId(): LiveData<Result<String>> {
        return authRepository.getUserId()
    }

    // Grup oluşturma fonksiyonu
    fun createGroup(groupName: String, creatorId: String): LiveData<Result<String>> {
        return groupRepository.createGroup(groupName, creatorId)
    }
}
