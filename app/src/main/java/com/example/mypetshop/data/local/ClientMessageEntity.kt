package com.example.mypetshop.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "client_messages")
data class ClientMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientEmail: String,
    val clientName: String,
    val orderId: String?,
    val subject: String,
    val body: String,
    val sentAtMillis: Long,
    val isFromAdmin: Boolean,
)
