package com.mertyigit0.secretsantaai.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.mertyigit0.secretsantaai.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class JoinGroupViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _joinGroupResult = MutableLiveData<Result<Unit>>()
    val joinGroupResult: LiveData<Result<Unit>> get() = _joinGroupResult

    fun joinGroup(groupId: String, Name: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return

        firestore.collection("groups").document(groupId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // members listesi artık Member objeleri içermeli
                    val members = document.get("users") as? List<*> ?: emptyList<Any>()
                    val alreadyJoined = members.any {
                        val member = it as? Map<*, *> // Firestore'dan gelen veriyi güvenli şekilde işliyoruz
                        member?.get("userId") == userId
                    }

                    if (!alreadyJoined) {
                        addUserToGroup(groupId, userId, Name)
                    } else {
                        _joinGroupResult.value = Result.failure(Exception("You are already a member of this group"))
                    }
                } else {
                    _joinGroupResult.value = Result.failure(Exception("Group not found"))
                }
            }
            .addOnFailureListener { exception ->
                _joinGroupResult.value = Result.failure(exception)
            }
    }

    private fun addUserToGroup(groupId: String, userId: String, name: String) {
        val memberData = User(userId = userId, username = name)

        // Member objesini ekliyoruz
        firestore.collection("groups").document(groupId)
            .update("users", FieldValue.arrayUnion(memberData))
            .addOnSuccessListener {
                addGroupToUser(userId, groupId)
            }
            .addOnFailureListener { exception ->
                _joinGroupResult.value = Result.failure(exception)
            }
    }

    private fun addGroupToUser(userId: String, groupId: String) {
        firestore.collection("users").document(userId)
            .update("groupsJoined", FieldValue.arrayUnion(groupId))
            .addOnSuccessListener {
                _joinGroupResult.value = Result.success(Unit)
            }
            .addOnFailureListener { exception ->
                _joinGroupResult.value = Result.failure(exception)
            }
    }
}
