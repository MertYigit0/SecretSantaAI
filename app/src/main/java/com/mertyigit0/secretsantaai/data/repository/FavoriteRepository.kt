package com.mertyigit0.secretsantaai.data.repository

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore'ı kullanabilmek için uygulama context'inde erişim sağlamak gerekir.
val Context.dataStore by preferencesDataStore(name = "favorites_data_store")

class FavoriteRepository(private val context: Context) {

    private object PreferencesKeys {
        val FAVORITE_ITEMS = stringSetPreferencesKey("favorite_items")
    }

    // Favori öğeleri almak
    val favoriteItems: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.FAVORITE_ITEMS] ?: emptySet()
        }

    // Favori öğeleri kaydetmek
    suspend fun addToFavorites(item: String) {
        context.dataStore.edit { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITE_ITEMS]?.toMutableSet() ?: mutableSetOf()
            currentFavorites.add(item)
            preferences[PreferencesKeys.FAVORITE_ITEMS] = currentFavorites
        }
    }

    // Favori öğeyi kaldırmak
    suspend fun removeFromFavorites(item: String) {
        context.dataStore.edit { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITE_ITEMS]?.toMutableSet() ?: mutableSetOf()
            currentFavorites.remove(item)
            preferences[PreferencesKeys.FAVORITE_ITEMS] = currentFavorites
        }
    }
}
