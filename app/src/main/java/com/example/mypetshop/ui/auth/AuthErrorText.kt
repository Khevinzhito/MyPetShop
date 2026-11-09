package com.example.mypetshop.ui.auth

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.mypetshop.R
import com.example.mypetshop.ui.theme.ErrorRed

@Composable
fun AuthErrorText(key: String) {
    val res = when (key) {
        "error_email_empty" -> R.string.error_email_empty
        "error_email_invalid" -> R.string.error_email_invalid
        "error_password_empty" -> R.string.error_password_empty
        "error_password_invalid" -> R.string.error_password_invalid
        "error_phone_invalid" -> R.string.error_phone_invalid
        "error_login_failed" -> R.string.error_login_failed
        "error_email_exists" -> R.string.error_email_exists
        "error_email_not_found" -> R.string.error_email_not_found
        "error_reset_code_invalid" -> R.string.error_reset_code_invalid
        "error_password_mismatch" -> R.string.error_password_mismatch
        "error_reset_failed" -> R.string.error_reset_failed
        "error_location_failed" -> R.string.error_location_failed
        "error_location_permission" -> R.string.error_location_permission
        "error_location_required" -> R.string.error_location_required
        "error_demo_password_missing" -> R.string.error_demo_password_missing
        else -> R.string.error_email_invalid
    }
    Text(stringResource(res), color = ErrorRed, style = MaterialTheme.typography.bodySmall)
}
