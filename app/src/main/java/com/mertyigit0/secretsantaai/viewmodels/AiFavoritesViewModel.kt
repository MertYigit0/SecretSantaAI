package com.mertyigit0.secretsantaai.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mertyigit0.secretsantaai.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AiFavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val favoriteRepository = FavoriteRepository(application)

    // Favorite öğelerinin Flow ile gözlemlenmesi
    val favoriteItems: Flow<Set<String>> = favoriteRepository.favoriteItems

    // Favoriye eklemek
    fun addToFavorites(item: String) {
        viewModelScope.launch {
            favoriteRepository.addToFavorites(item)
        }
    }

    // Favorilerden çıkarmak
    fun removeFromFavorites(item: String) {
        viewModelScope.launch {
            favoriteRepository.removeFromFavorites(item)
        }
    }
}
