package com.mertyigit0.secretsantaai.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    fun logout() {
        auth.signOut()
    }

    fun isUserLoggedOut(): Boolean {
        return auth.currentUser == null
    }
}
