package com.example.mypetshop.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypetshop.data.session.ShopSession
import com.example.mypetshop.domain.model.ClientMessage
import com.example.mypetshop.domain.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MessageComposeState(
    val clientEmail: String = "",
    val clientName: String = "",
    val orderId: String = "",
    val subject: String = "",
    val body: String = "",
    val errorKey: String? = null,
    val sentSuccess: Boolean = false,
)

@HiltViewModel
class AdminMessagesViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val shopSession: ShopSession,
) : ViewModel() {

    val messages = messageRepository
        .observeAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _composeState = MutableStateFlow(MessageComposeState())
    val composeState: StateFlow<MessageComposeState> = _composeState.asStateFlow()

    fun availableClients(): List<Pair<String, String>> = shopSession.observeClientEmails()

    fun onClientSelected(email: String, name: String) {
        _composeState.update { it.copy(clientEmail = email, clientName = name, errorKey = null) }
    }

    fun onOrderIdChange(value: String) = _composeState.update { it.copy(orderId = value, errorKey = null) }
    fun onSubjectChange(value: String) = _composeState.update { it.copy(subject = value, errorKey = null) }
    fun onBodyChange(value: String) = _composeState.update { it.copy(body = value, errorKey = null) }

    fun sendMessage() {
        val state = _composeState.value
        when {
            state.clientEmail.isBlank() -> _composeState.update { it.copy(errorKey = "error_message_client") }
            state.subject.isBlank() -> _composeState.update { it.copy(errorKey = "error_message_subject") }
            state.body.length < 5 -> _composeState.update { it.copy(errorKey = "error_message_body") }
            else -> viewModelScope.launch {
                messageRepository.sendAdminMessage(
                    ClientMessage(
                        clientEmail = state.clientEmail.trim(),
                        clientName = state.clientName.ifBlank { state.clientEmail.substringBefore("@") },
                        orderId = state.orderId.trim().ifBlank { null },
                        subject = state.subject.trim(),
                        body = state.body.trim(),
                        sentAtMillis = System.currentTimeMillis(),
                        isFromAdmin = true,
                    ),
                )
                _composeState.value = MessageComposeState(sentSuccess = true)
            }
        }
    }

    fun resetCompose() {
        _composeState.value = MessageComposeState()
    }
}
