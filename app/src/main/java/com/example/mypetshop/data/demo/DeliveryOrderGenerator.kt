package com.example.mypetshop.data.demo

import com.example.mypetshop.domain.model.DeliveryOrder
import com.example.mypetshop.domain.model.DeliveryStatus
import kotlin.random.Random

object DeliveryOrderGenerator {

    private val products = listOf(
        "Advance Snack Dental 180 g",
        "Logus Premio Natural 150 g",
        "Advance Snack Training",
        "Advance Adult Medium 15 kg",
        "Advance Sensitive Salmón 3 kg",
    )

    private val clientAddresses = listOf(
        "Calle 45 #12-34, Chapinero, Bogotá",
        "Carrera 7 #80-20, El Chicó, Bogotá",
        "Av. 68 #25-10, Salitre, Bogotá",
        "Calle 127 #15-40, Usaquén, Bogotá",
        "Carrera 15 #93-55, Chico Norte, Bogotá",
        "Calle 85 #11-42, La Cabrera, Bogotá",
    )

    private val driverAddresses = listOf(
        "Centro de despacho Logus, Av. El Dorado #68C-61, Bogotá",
        "Bodega MyPetShop, Calle 26 #69-70, Bogotá",
        "Punto reparto Norte, Autopista Norte #145-20, Bogotá",
        "Hub Chapinero, Carrera 13 #58-10, Bogotá",
        "Base sur Logus, Carrera 30 #7-45, Bogotá",
    )

    fun generate(count: Int = 3): List<DeliveryOrder> {
        val random = Random(System.currentTimeMillis())
        return (1..count).map { index ->
            val phoneSuffix = random.nextInt(1000, 9999)
            val hour = random.nextInt(10, 20)
            val minute = listOf(0, 15, 30, 45).random(random)
            DeliveryOrder(
                id = "PED-${random.nextInt(10000, 99999)}",
                clientPhoneMasked = "*** *** $phoneSuffix",
                productName = products.random(random),
                clientAddress = clientAddresses.random(random),
                driverAddress = driverAddresses.random(random),
                confirmByTime = "%02d:%02d".format(hour, minute),
                status = if (index == 1) DeliveryStatus.IN_TRANSIT else DeliveryStatus.PENDING,
            )
        }
    }
}
