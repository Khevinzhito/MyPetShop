package com.example.mypetshop.util

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Hashing de contrasenas con PBKDF2-HMAC-SHA256.
 *
 * Formato almacenado:  "pbkdf2_sha256${iterations}${saltBase64}${hashBase64}"
 *
 * - No depende de librerias externas: usa javax.crypto disponible en Android.
 * - Salt aleatorio de 16 bytes por contrasena.
 * - 120_000 iteraciones (recomendacion OWASP 2023 para PBKDF2-SHA256).
 * - Comparacion en tiempo constante para evitar timing attacks.
 */
object PasswordHasher {

    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH_BITS = 256
    private const val SALT_BYTES = 16
    private const val PREFIX = "pbkdf2_sha256"

    fun hash(password: String): String {
        require(password.isNotEmpty()) { "password vacio" }
        val salt = ByteArray(SALT_BYTES).also { SecureRandom().nextBytes(it) }
        val hash = derive(password, salt, ITERATIONS)
        return buildString {
            append(PREFIX)
            append('$').append(ITERATIONS)
            append('$').append(encode(salt))
            append('$').append(encode(hash))
        }
    }

    fun verify(password: String, stored: String): Boolean {
        if (stored.isBlank() || password.isEmpty()) return false
        val parts = stored.split('$')
        if ((parts.size != 4) || (parts[0] != PREFIX)) {
            return legacyEquals(password, stored)
        }
        val iterations = parts[1].toIntOrNull() ?: return false
        val salt = runCatching { decode(parts[2]) }.getOrNull() ?: return false
        val expected = runCatching { decode(parts[3]) }.getOrNull() ?: return false
        val actual = derive(password, salt, iterations)
        return constantTimeEquals(expected, actual)
    }

    /**
     * Permite que cuentas heredadas (texto plano antes del refactor) sigan funcionando
     * una sola vez para que el usuario pueda re-loguearse; el repositorio reescribira el hash.
     * Si el formato del campo no es PBKDF2, se comparan los Strings directamente.
     */
    private fun legacyEquals(password: String, stored: String): Boolean =
        stored == password

    private fun derive(password: String, salt: ByteArray, iterations: Int): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH_BITS)
        return try {
            SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun encode(bytes: ByteArray): String =
        Base64.encodeToString(bytes, Base64.NO_WRAP or Base64.NO_PADDING)

    private fun decode(value: String): ByteArray =
        Base64.decode(value, Base64.NO_WRAP or Base64.NO_PADDING)

    private fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false
        var diff = 0
        for (i in a.indices) diff = diff or (a[i].toInt() xor b[i].toInt())
        return diff == 0
    }
}
