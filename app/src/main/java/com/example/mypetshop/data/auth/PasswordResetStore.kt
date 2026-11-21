package com.example.mypetshop.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

private val Context.passwordResetDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "password_reset",
)

@Singleton
class PasswordResetStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val emailKey = stringPreferencesKey("reset_email")
    private val codeKey = stringPreferencesKey("reset_code")
    private val expiresKey = longPreferencesKey("reset_expires")

    suspend fun createResetCode(email: String): String {
        val code = (100000..999999).random(Random.Default).toString()
        val expiresAt = System.currentTimeMillis() + CODE_TTL_MS
        context.passwordResetDataStore.edit { prefs ->
            prefs[emailKey] = email.trim().lowercase()
            prefs[codeKey] = code
            prefs[expiresKey] = expiresAt
        }
        return code
    }

    suspend fun verifyCode(email: String, code: String): Boolean {
        val prefs = context.passwordResetDataStore.data.first()
        val storedEmail = prefs[emailKey] ?: return false
        val storedCode = prefs[codeKey] ?: return false
        val expiresAt = prefs[expiresKey] ?: return false
        return storedEmail == email.trim().lowercase() &&
            storedCode == code.trim() &&
            System.currentTimeMillis() <= expiresAt
    }

    suspend fun clear() {
        context.passwordResetDataStore.edit { it.clear() }
    }

    companion object {
        private const val CODE_TTL_MS = 15 * 60 * 1000L
    }
}
