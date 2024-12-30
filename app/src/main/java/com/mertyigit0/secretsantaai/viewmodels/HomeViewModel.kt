package com.mertyigit0.secretsantaai.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mertyigit0.secretsantaai.data.model.Group
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _groups = MutableLiveData<List<Group>>()
    val groups: LiveData<List<Group>> get() = _groups

    init {
        fetchUserGroups()
    }

    private fun fetchUserGroups() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                val groupsJoined = document.get("groupsJoined") as? List<String> ?: emptyList()
                val groupsCreated = document.get("groupsCreated") as? List<String> ?: emptyList()

                val allGroupIds = groupsJoined +groupsCreated
                if (allGroupIds.isNotEmpty()) {
                    fetchGroupsDetails(allGroupIds)
                } else {
                    _groups.value = emptyList()
                }
            }
    }

    private fun fetchGroupsDetails(groupsIds: List<String>) {
        val groups = mutableListOf<Group>()
        val groupCollection = firestore.collection("groups")

        // Fetch details for each group the user has joined or created
        groupsIds.forEach { groupId ->
            groupCollection.document(groupId).get()
                .addOnSuccessListener { document ->
                    val group = document.toObject(Group::class.java)
                    if (group != null) {
                        groups.add(group)
                    }
                    // If all groups are fetched, update the LiveData
                    if (groups.size == groupsIds.size) {
                        _groups.value = groups
                    }
                }
        }
    }
}

