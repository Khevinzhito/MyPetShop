package com.example.mypetshop.data.seed

import com.example.mypetshop.domain.model.ProductCategory
import com.example.mypetshop.domain.model.ProductPetType
import com.example.mypetshop.domain.model.ShopProduct

object PetFoodSeedCatalog {

    val products: List<ShopProduct> by lazy { premiumFoods + standardFoods + snacks }

    private val premiumFoods = listOf(
        product("adv-dog-001", "Advance Adulto Pollo Mediano", "Nutrición premium Logus para perros adultos de razas medianas. Bolsa 14 kg.", 189900, "Advance", ProductCategory.PREMIUM, ProductPetType.DOG),
        product("adv-cat-001", "Advance Gato Esterilizado Salmón", "Fórmula Advance para gatos esterilizados con salmón. Bolsa 3 kg.", 124500, "Advance", ProductCategory.PREMIUM, ProductPetType.CAT),
        product("adv-dog-002", "Advance Sensitive Cordero", "Alimento hipoalergénico Advance para perros sensibles. Bolsa 12 kg.", 175000, "Advance", ProductCategory.PREMIUM, ProductPetType.DOG),
        product("totw-dog-001", "Taste of the Wild High Prairie", "Proteína de bisonte y venado, sin granos. Bolsa 12.2 kg.", 298000, "Taste of the Wild", ProductCategory.PREMIUM, ProductPetType.DOG),
        product("totw-cat-001", "Taste of the Wild Rocky Mountain", "Salmón ahumado y trucha para gatos. Bolsa 2 kg.", 165000, "Taste of the Wild", ProductCategory.PREMIUM, ProductPetType.CAT),
        product("agil-dog-001", "Agility Gold Adulto Razas Medianas", "Balance premium para perros activos. Bolsa 15 kg.", 142000, "Agility Gold", ProductCategory.PREMIUM, ProductPetType.DOG),
        product("agil-cat-001", "Agility Gold Gato Indoor", "Control de peso para gatos de interior. Bolsa 3 kg.", 98000, "Agility Gold", ProductCategory.PREMIUM, ProductPetType.CAT),
        product("max-dog-001", "Max Premium Adulto Pollo", "Línea premium Max con proteína de pollo. Bolsa 15 kg.", 118000, "Max", ProductCategory.PREMIUM, ProductPetType.DOG),
        product("br-dog-001", "BR Premium Cordero y Arroz", "Receta premium BR para digestión óptima. Bolsa 15 kg.", 112000, "BR", ProductCategory.PREMIUM, ProductPetType.DOG),
        product("br-cat-001", "BR Premium Gato Salmón", "Nutrición completa premium para gatos. Bolsa 3 kg.", 89000, "BR", ProductCategory.PREMIUM, ProductPetType.CAT),
        product("evol-dog-001", "Evolve Pollo y Arroz Adulto", "Alimento super premium Evolve. Bolsa 15 kg.", 135000, "Evolve", ProductCategory.PREMIUM, ProductPetType.DOG),
        product("evol-cat-001", "Evolve Gato Pollo", "Evolve premium para gatos adultos. Bolsa 3 kg.", 92000, "Evolve", ProductCategory.PREMIUM, ProductPetType.CAT),
        product("excl-dog-001", "Excellent Premium Salmón", "Excelente digestibilidad con salmón premium. Bolsa 15 kg.", 128000, "Excellent", ProductCategory.PREMIUM, ProductPetType.DOG),
        product("excl-cat-001", "Excellent Premium Gato", "Fórmula premium Excellent para felinos. Bolsa 3 kg.", 85000, "Excellent", ProductCategory.PREMIUM, ProductPetType.CAT),
        product("chk-dog-001", "Chunky Premium Cordero", "Chunky línea premium para perros adultos. Bolsa 15 kg.", 108000, "Chunky", ProductCategory.PREMIUM, ProductPetType.DOG),
        product("chk-cat-001", "Chunky Premium Gato Mix", "Receta premium Chunky para gatos. Bolsa 3 kg.", 82000, "Chunky", ProductCategory.PREMIUM, ProductPetType.CAT),
    )

    private val standardFoods = listOf(
        product("dc-dog-001", "Dog Chow Adulto Pollo", "Alimento completo y balanceado para perros adultos. Bolsa 21 kg.", 145000, "Dog Chow", ProductCategory.STANDARD, ProductPetType.DOG),
        product("dc-dog-002", "Dog Chow Cachorro Pollo", "Crecimiento saludable para cachorros. Bolsa 8 kg.", 72000, "Dog Chow", ProductCategory.STANDARD, ProductPetType.DOG),
        product("chk-dog-002", "Chunky Adulto Carne y Cereales", "Alimento económico Chunky para perros. Bolsa 8 kg.", 48000, "Chunky", ProductCategory.STANDARD, ProductPetType.DOG),
        product("chk-cat-002", "Chunky Gato Pescado", "Alimento Chunky para gatos adultos. Bolsa 1.5 kg.", 32000, "Chunky", ProductCategory.STANDARD, ProductPetType.CAT),
        product("max-dog-002", "Max Normal Adulto Pollo", "Línea estándar Max para perros. Bolsa 15 kg.", 78000, "Max", ProductCategory.STANDARD, ProductPetType.DOG),
        product("max-cat-001", "Max Normal Gato Pescado", "Max estándar para gatos. Bolsa 3 kg.", 45000, "Max", ProductCategory.STANDARD, ProductPetType.CAT),
        product("br-dog-002", "BR Adulto Pollo y Arroz", "BR línea estándar para perros. Bolsa 15 kg.", 68000, "BR", ProductCategory.STANDARD, ProductPetType.DOG),
        product("br-cat-002", "BR Gato Pollo", "BR estándar para gatos. Bolsa 3 kg.", 42000, "BR", ProductCategory.STANDARD, ProductPetType.CAT),
        product("evol-dog-002", "Evolve Adulto Estándar", "Evolve línea accesible para perros. Bolsa 15 kg.", 88000, "Evolve", ProductCategory.STANDARD, ProductPetType.DOG),
        product("excl-dog-002", "Excellent Adulto Pollo", "Excellent estándar para perros. Bolsa 8 kg.", 55000, "Excellent", ProductCategory.STANDARD, ProductPetType.DOG),
        product("agil-dog-002", "Agility Gold Estándar Adulto", "Agility Gold línea económica. Bolsa 15 kg.", 95000, "Agility Gold", ProductCategory.STANDARD, ProductPetType.DOG),
    )

    private val snacks = listOf(
        product("snack-001", "Advance Snack Dental", "Premio funcional Logus para higiene dental. Paquete 180 g.", 28900, "Logus", ProductCategory.SNACK, ProductPetType.BOTH),
        product("snack-002", "Advance Snack Training", "Snack premium para entrenamiento con proteína de pollo.", 24500, "Logus", ProductCategory.SNACK, ProductPetType.DOG),
        product("snack-003", "Logus Premio Natural", "Snack 100% natural sin colorantes artificiales. 150 g.", 19900, "Logus", ProductCategory.SNACK, ProductPetType.BOTH),
        product("snack-004", "Advance Snack Sensitive", "Ideal para mascotas con digestión sensible. 120 g.", 26500, "Logus", ProductCategory.SNACK, ProductPetType.BOTH),
        product("snack-005", "Logus Mini Bones", "Huesitos masticables enriquecidos con calcio.", 17900, "Logus", ProductCategory.SNACK, ProductPetType.DOG),
        product("snack-006", "Advance Snack Hairball", "Control de bolas de pelo para gatos. 60 g.", 22100, "Logus", ProductCategory.SNACK, ProductPetType.CAT),
    )

    private fun product(
        id: String,
        name: String,
        description: String,
        priceCents: Long,
        brand: String,
        category: ProductCategory,
        petType: ProductPetType,
    ) = ShopProduct(
        id = id,
        name = name,
        description = description,
        priceCents = priceCents,
        brand = brand,
        category = category,
        petType = petType,
    )
}
