package com.example.mypetshop.data.auth

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

data class RecentAccount(
    val email: String,
    val displayName: String,
    val role: String,
)

@Singleton
class RecentAccountStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val prefs = context.getSharedPreferences("mypetshop_recent_accounts", Context.MODE_PRIVATE)

    fun getRecentAccounts(): List<RecentAccount> {
        val jsonStr = prefs.getString("accounts_list", "[]") ?: "[]"
        val list = mutableListOf<RecentAccount>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    RecentAccount(
                        email = obj.getString("email"),
                        displayName = obj.getString("displayName"),
                        role = obj.getString("role"),
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun addAccount(email: String, displayName: String, role: String) {
        val current = getRecentAccounts().toMutableList()
        current.removeAll { it.email.equals(email, ignoreCase = true) }
        current.add(0, RecentAccount(email, displayName, role))
        saveList(current)
    }

    fun removeAccount(email: String) {
        val current = getRecentAccounts().toMutableList()
        current.removeAll { it.email.equals(email, ignoreCase = true) }
        saveList(current)
    }

    private fun saveList(list: List<RecentAccount>) {
        val jsonArray = JSONArray()
        for (acc in list) {
            val obj = JSONObject().apply {
                put("email", acc.email)
                put("displayName", acc.displayName)
                put("role", acc.role)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString("accounts_list", jsonArray.toString()).apply()
    }
}
