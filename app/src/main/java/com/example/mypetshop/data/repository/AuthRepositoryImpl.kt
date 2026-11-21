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
        val requiredSeeds = listOf(
            UserEntity(
                email = DemoAccounts.BUYER_EMAIL.trim().lowercase(),
                password = PasswordHasher.hash("10251025Aa"),
                displayName = DemoAccounts.BUYER_NAME,
                role = UserRole.BUYER.name,
                phone = "",
            ),
            UserEntity(
                email = DemoAccounts.ADMIN_EMAIL.trim().lowercase(),
                password = PasswordHasher.hash("Admin1234"),
                displayName = DemoAccounts.ADMIN_NAME,
                role = UserRole.ADMIN.name,
                phone = "",
            ),
            UserEntity(
                email = DemoAccounts.DRIVER_EMAIL.trim().lowercase(),
                password = PasswordHasher.hash("10251025Aa"),
                displayName = DemoAccounts.DRIVER_NAME,
                role = UserRole.DRIVER.name,
                phone = DemoAccounts.DRIVER_PHONE,
            ),
        )
        for (user in requiredSeeds) {
            val existing = userDao.findByEmail(user.email)
            if (existing == null) {
                userDao.insert(user)
            } else {
                userDao.updatePassword(user.email, user.password)
            }
        }
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
        securityQuestion: String,
        securityAnswer: String,
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
                securityQuestion = securityQuestion.trim(),
                securityAnswer = securityAnswer.trim().lowercase(),
            ),
        )
        return RegisterResult.Success
    }

    override suspend fun verifySecurityAnswer(email: String, answer: String): Boolean {
        seedIfEmpty()
        val entity = userDao.findByEmail(email.trim().lowercase()) ?: return false
        return entity.securityAnswer.isNotBlank() && entity.securityAnswer.equals(answer.trim(), ignoreCase = true)
    }

    override suspend fun getSecurityQuestion(email: String): String? {
        seedIfEmpty()
        val entity = userDao.findByEmail(email.trim().lowercase()) ?: return null
        return entity.securityQuestion.ifBlank { "Pregunta de seguridad no registrada" }
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
