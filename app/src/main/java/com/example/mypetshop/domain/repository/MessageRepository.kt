package com.example.mypetshop.domain.repository

import com.example.mypetshop.domain.model.ClientMessage
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun observeAllMessages(): Flow<List<ClientMessage>>
    fun observeMessagesForClient(clientEmail: String): Flow<List<ClientMessage>>
    suspend fun sendAdminMessage(message: ClientMessage)
}
