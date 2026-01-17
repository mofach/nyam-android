package com.project.nyam.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_session")

class SessionManager(private val context: Context) {
    companion object {
        val USER_ID = stringPreferencesKey("user_id")
        val IS_ONBOARDING_DONE = booleanPreferencesKey("is_onboarding_done")
    }

    // Simpan data setelah login berhasil
    suspend fun saveSession(uid: String, isDone: Boolean) {
        context.dataStore.edit { it[USER_ID] = uid; it[IS_ONBOARDING_DONE] = isDone }
    }

    // Ambil status untuk navigasi
    val isOnboardingDone: Flow<Boolean> = context.dataStore.data.map { it[IS_ONBOARDING_DONE] ?: false }
}