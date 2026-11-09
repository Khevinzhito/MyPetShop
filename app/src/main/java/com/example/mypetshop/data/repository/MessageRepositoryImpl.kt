package com.example.mypetshop.data.repository

import com.example.mypetshop.data.local.ClientMessageDao
import com.example.mypetshop.data.local.toDomain
import com.example.mypetshop.data.local.toEntity
import com.example.mypetshop.domain.model.ClientMessage
import com.example.mypetshop.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val messageDao: ClientMessageDao,
) : MessageRepository {

    override fun observeAllMessages(): Flow<List<ClientMessage>> =
        messageDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeMessagesForClient(clientEmail: String): Flow<List<ClientMessage>> =
        messageDao.observeForClient(clientEmail).map { list -> list.map { it.toDomain() } }

    override suspend fun sendAdminMessage(message: ClientMessage) {
        messageDao.insert(message.toEntity())
    }
}
