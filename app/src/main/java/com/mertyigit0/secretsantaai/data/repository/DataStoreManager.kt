package com.mertyigit0.secretsantaai.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.languageDataStore: DataStore<Preferences> by preferencesDataStore(name = "language_data_store")

@Singleton
class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val languageKey = stringPreferencesKey("language")

    suspend fun saveLanguage(languageCode: String) {
        context.languageDataStore.edit { preferences ->
            preferences[languageKey] = languageCode
        }
    }

    val languageFlow: Flow<String> = context.languageDataStore.data
        .map { preferences ->
            preferences[languageKey] ?: "en"
        }
}
