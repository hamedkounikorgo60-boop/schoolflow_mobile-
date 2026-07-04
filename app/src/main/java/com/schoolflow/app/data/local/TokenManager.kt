package com.schoolflow.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "schoolflow_prefs")

class TokenManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_ROLE_KEY = stringPreferencesKey("user_role")
        private val ELEVE_ID_KEY = stringPreferencesKey("eleve_id")
    }

    suspend fun saveToken(token: String, userName: String, role: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USER_NAME_KEY] = userName
            prefs[USER_ROLE_KEY] = role
        }
    }

    suspend fun saveEleveId(eleveId: Int) {
        context.dataStore.edit { prefs ->
            prefs[ELEVE_ID_KEY] = eleveId.toString()
        }
    }

    val token: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val userName: Flow<String?> = context.dataStore.data.map { it[USER_NAME_KEY] }
    val userRole: Flow<String?> = context.dataStore.data.map { it[USER_ROLE_KEY] }
    val eleveId: Flow<String?> = context.dataStore.data.map { it[ELEVE_ID_KEY] }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
