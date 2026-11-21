package com.example.mypetshop.domain.model

data class SalesSummary(
    val totalSalesCents: Long = 0,
    val completedSalesCents: Long = 0,
    val pendingSalesCents: Long = 0,
    val totalOrders: Int = 0,
    val completedOrders: Int = 0,
    val pendingOrders: Int = 0,
    val estimatedProfitCents: Long = 0,
)
