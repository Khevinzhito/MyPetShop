package com.example.mypetshop.data.local

import com.example.mypetshop.domain.model.ClientMessage

fun ClientMessageEntity.toDomain(): ClientMessage = ClientMessage(
    id = id,
    clientEmail = clientEmail,
    clientName = clientName,
    orderId = orderId,
    subject = subject,
    body = body,
    sentAtMillis = sentAtMillis,
    isFromAdmin = isFromAdmin,
)

fun ClientMessage.toEntity(): ClientMessageEntity = ClientMessageEntity(
    id = id,
    clientEmail = clientEmail,
    clientName = clientName,
    orderId = orderId,
    subject = subject,
    body = body,
    sentAtMillis = sentAtMillis,
    isFromAdmin = isFromAdmin,
)
