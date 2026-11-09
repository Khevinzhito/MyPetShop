package com.example.mypetshop.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.biometricDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "biometric_login",
)

@Singleton
class BiometricCredentialStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val enabledKey = booleanPreferencesKey("biometric_enabled")
    private val emailKey = stringPreferencesKey("biometric_email")

    suspend fun isEnabled(): Boolean =
        context.biometricDataStore.data.first()[enabledKey] == true

    suspend fun getStoredEmail(): String? =
        context.biometricDataStore.data.first()[emailKey]

    suspend fun saveCredential(email: String) {
        context.biometricDataStore.edit { prefs ->
            prefs[enabledKey] = true
            prefs[emailKey] = email.trim().lowercase()
        }
    }

    suspend fun clear() {
        context.biometricDataStore.edit { it.clear() }
    }
}
