package com.example.mypetshop.data.repository

import com.example.mypetshop.data.demo.DemoAccounts
import com.example.mypetshop.data.local.UserDao
import com.example.mypetshop.data.local.UserEntity
import com.example.mypetshop.domain.model.AuthUser
import com.example.mypetshop.domain.model.UserRole
import com.example.mypetshop.domain.repository.AuthRepository
import com.example.mypetshop.domain.repository.RegisterResult
import com.example.mypetshop.util.AuthValidator
import com.example.mypetshop.util.PasswordHasher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
) : AuthRepository {

    override suspend fun seedIfEmpty() {
        if (userDao.count() > 0) return
        val seeded = DemoAccounts.seeds()
            .filter { it.hasPassword }
            .map { seed ->
                UserEntity(
                    email = seed.email.trim().lowercase(),
                    password = PasswordHasher.hash(seed.rawPassword),
                    displayName = seed.displayName,
                    role = seed.role.name,
                    phone = seed.phone,
                )
            }
        if (seeded.isNotEmpty()) userDao.insertAll(seeded)
    }

    override suspend fun login(email: String, password: String): AuthUser? {
        seedIfEmpty()
        val normalizedEmail = email.trim().lowercase()
        val entity = userDao.findByEmail(normalizedEmail) ?: return null
        if (!PasswordHasher.verify(password, entity.password)) return null
        // Migracion silenciosa: si la contrasena estaba en texto plano (legado),
        // la reescribimos como hash PBKDF2.
        if (!entity.password.startsWith("pbkdf2_sha256$")) {
            userDao.updatePassword(normalizedEmail, PasswordHasher.hash(password))
        }
        return entity.toDomain()
    }

    override suspend fun register(
        email: String,
        password: String,
        phone: String,
        displayName: String,
        role: UserRole,
    ): RegisterResult {
        seedIfEmpty()
        val normalizedEmail = email.trim().lowercase()
        if (userDao.findByEmail(normalizedEmail) != null) {
            return RegisterResult.EmailAlreadyExists
        }
        userDao.insert(
            UserEntity(
                email = normalizedEmail,
                password = PasswordHasher.hash(password),
                displayName = displayName.trim(),
                role = role.name,
                phone = phone.trim(),
            ),
        )
        return RegisterResult.Success
    }

    override suspend fun userExists(email: String): Boolean {
        seedIfEmpty()
        return userDao.findByEmail(email.trim().lowercase()) != null
    }

    override suspend fun resetPassword(email: String, newPassword: String): Boolean {
        val normalizedEmail = email.trim().lowercase()
        if (AuthValidator.validatePassword(newPassword) != null) return false
        if (userDao.findByEmail(normalizedEmail) == null) return false
        userDao.updatePassword(normalizedEmail, PasswordHasher.hash(newPassword))
        return true
    }

    override suspend fun getUserByEmail(email: String): AuthUser? {
        seedIfEmpty()
        return userDao.findByEmail(email.trim().lowercase())?.toDomain()
    }

    private fun UserEntity.toDomain() = AuthUser(
        email = email,
        displayName = displayName,
        role = UserRole.valueOf(role),
        phone = phone,
    )
}
