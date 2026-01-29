package com.project.nyam.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.project.nyam.data.model.NutritionalNeeds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_session")

class SessionManager(private val context: Context) {
    companion object {
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val IS_ONBOARDING_DONE = booleanPreferencesKey("is_onboarding_done")
        val CALORIES = intPreferencesKey("calories")
        val CARBS = intPreferencesKey("carbs")
        val PROTEIN = intPreferencesKey("protein")
        val FAT = intPreferencesKey("fat")
    }

    // Berikan default value agar panggillan lama tidak error
    suspend fun saveSession(
        uid: String,
        name: String? = null,
        isDone: Boolean = false,
        needs: NutritionalNeeds? = null
    ) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = uid
            if (name != null) prefs[USER_NAME] = name
            prefs[IS_ONBOARDING_DONE] = isDone

            needs?.let {
                prefs[CALORIES] = it.calories
                prefs[CARBS] = it.carbs
                prefs[PROTEIN] = it.protein
                prefs[FAT] = it.fat
            }
        }
    }

    val isOnboardingDone: Flow<Boolean> = context.dataStore.data.map { it[IS_ONBOARDING_DONE] ?: false }
    val nutritionalNeeds: Flow<NutritionalNeeds?> = context.dataStore.data.map { prefs ->
        val cal = prefs[CALORIES] ?: 0
        if (cal == 0) null else NutritionalNeeds(
            calories = cal,
            carbs = prefs[CARBS] ?: 0,
            protein = prefs[PROTEIN] ?: 0,
            fat = prefs[FAT] ?: 0
        )
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}