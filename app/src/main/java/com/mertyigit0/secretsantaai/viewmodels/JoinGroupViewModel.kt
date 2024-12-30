package com.mertyigit0.secretsantaai.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.mertyigit0.secretsantaai.data.model.Group
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class JoinGroupViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _joinGroupResult = MutableLiveData<Result<Unit>>()
    val joinGroupResult: LiveData<Result<Unit>> get() = _joinGroupResult

    fun joinGroup(groupId: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return

        firestore.collection("groups").document(groupId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val group = document.toObject(Group::class.java)
                    if (group != null && !group.members.contains(userId)) {
                        // Grup mevcut ve kullanıcı grupta değilse, kullanıcıyı ekle
                        addUserToGroup(groupId, userId)
                    } else {
                        // Kullanıcı zaten grupta veya grup yok
                        _joinGroupResult.value = Result.failure(Exception("You are already a member of this group or group does not exist"))
                    }
                } else {
                    // Grup bulunamadı
                    _joinGroupResult.value = Result.failure(Exception("Group not found"))
                }
            }
            .addOnFailureListener { exception ->
                _joinGroupResult.value = Result.failure(exception)
            }
    }

    private fun addUserToGroup(groupId: String, userId: String) {
        // Kullanıcıyı gruba ekle
        firestore.collection("groups").document(groupId)
            .update("members", FieldValue.arrayUnion(userId))
            .addOnSuccessListener {
                // Kullanıcıyı başarılı bir şekilde gruba ekledik, şimdi kullanıcıyı da 'groupsJoined' alanına ekle
                addGroupToUser(userId, groupId)
            }
            .addOnFailureListener { exception ->
                _joinGroupResult.value = Result.failure(exception)
            }
    }

    private fun addGroupToUser(userId: String, groupId: String) {
        // Kullanıcıya ait 'groupsJoined' koleksiyonunu güncelle
        firestore.collection("users").document(userId)
            .update("groupsJoined", FieldValue.arrayUnion(groupId))
            .addOnSuccessListener {
                // Kullanıcıya grubu ekledik
                _joinGroupResult.value = Result.success(Unit)
            }
            .addOnFailureListener { exception ->
                _joinGroupResult.value = Result.failure(exception)
            }
    }
}
