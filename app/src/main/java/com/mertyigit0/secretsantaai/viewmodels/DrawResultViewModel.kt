package com.mertyigit0.secretsantaai.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "draw_results")

@HiltViewModel
class DrawResultViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val dataStore = context.dataStore
    private val _giftReceiver = MutableLiveData<String?>()
    val giftReceiver: LiveData<String?> get() = _giftReceiver

    fun fetchDrawResult(userId: String, groupId: String) {
        viewModelScope.launch {
            val savedResult = getSavedResult(userId, groupId)
            if (savedResult != null) {
                _giftReceiver.postValue(savedResult)
            } else {
                firestore.collection("groups").document(groupId).get()
                    .addOnSuccessListener { document ->
                        val drawResults = document.get("drawResults") as? Map<String, String>
                        val recipientId = drawResults?.get(userId)
                        if (recipientId != null) {
                            fetchRecipientName(userId, groupId, recipientId)
                        }
                    }
            }
        }
    }

    private fun fetchRecipientName(userId: String, groupId: String, recipientId: String) {
        firestore.collection("users").document(recipientId).get()
            .addOnSuccessListener { document ->
                val recipientName = document.getString("username")
                _giftReceiver.postValue(recipientName)
                viewModelScope.launch {
                    if (recipientName != null) {
                        saveResult(userId, groupId, recipientName)
                    }
                }
            }
    }

    private suspend fun saveResult(userId: String, groupId: String, recipientName: String) {
        val key = stringPreferencesKey("$userId-$groupId")
        dataStore.edit { preferences ->
            preferences[key] = recipientName
        }
    }

    private suspend fun getSavedResult(userId: String, groupId: String): String? {
        val key = stringPreferencesKey("$userId-$groupId")
        val preferences = dataStore.data.first()
        return preferences[key]
    }
}
