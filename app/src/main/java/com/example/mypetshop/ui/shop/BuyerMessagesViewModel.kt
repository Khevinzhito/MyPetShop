package com.example.mypetshop.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypetshop.data.session.NavigationSession
import com.example.mypetshop.domain.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BuyerMessagesViewModel @Inject constructor(
    messageRepository: MessageRepository,
    private val navigationSession: NavigationSession,
) : ViewModel() {

    val messages = messageRepository
        .observeMessagesForClient(navigationSession.currentUser?.email.orEmpty())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
