package com.example.mypetshop.data.tips

import com.example.mypetshop.domain.model.PetProfile
import com.example.mypetshop.domain.model.PetType
import com.example.mypetshop.domain.model.ProductCategory
import com.example.mypetshop.domain.model.ProductPetType
import com.example.mypetshop.domain.model.ShopProduct

enum class PetTipType {
    TRANSITION,
    DAILY,
    HEALTH,
    FOOD,
}

data class PetTip(
    val type: PetTipType,
    val titleRes: Int,
    val messageRes: Int,
    val formatArgs: List<Any> = emptyList(),
)

object PetTipsEngine {

    fun buildTips(profile: PetProfile): List<PetTip> {
        val tips = mutableListOf<PetTip>()

        if (profile.wantsAdvanceTransition) {
            tips += PetTip(
                type = PetTipType.TRANSITION,
                titleRes = com.example.mypetshop.R.string.pet_tip_transition_title,
                messageRes = com.example.mypetshop.R.string.pet_tip_transition_body,
                formatArgs = listOf(profile.currentFood, profile.breed),
            )
            tips += PetTip(
                type = PetTipType.TRANSITION,
                titleRes = com.example.mypetshop.R.string.pet_tip_transition_steps_title,
                messageRes = com.example.mypetshop.R.string.pet_tip_transition_steps_body,
            )
        }

        tips += PetTip(
            type = PetTipType.DAILY,
            titleRes = com.example.mypetshop.R.string.pet_tip_daily_title,
            messageRes = if (profile.petType == PetType.DOG) {
                com.example.mypetshop.R.string.pet_tip_daily_dog_body
            } else {
                com.example.mypetshop.R.string.pet_tip_daily_cat_body
            },
            formatArgs = listOf(profile.breed, profile.ageYears),
        )

        if (profile.isSterilized) {
            tips += PetTip(
                type = PetTipType.HEALTH,
                titleRes = com.example.mypetshop.R.string.pet_tip_sterilized_title,
                messageRes = if (profile.petType == PetType.DOG) {
                    com.example.mypetshop.R.string.pet_tip_sterilized_dog_body
                } else {
                    com.example.mypetshop.R.string.pet_tip_sterilized_cat_body
                },
            )
        }

        if (profile.hasIllness && profile.illnessDescription.isNotBlank()) {
            tips += PetTip(
                type = PetTipType.HEALTH,
                titleRes = com.example.mypetshop.R.string.pet_tip_illness_title,
                messageRes = com.example.mypetshop.R.string.pet_tip_illness_body,
                formatArgs = listOf(profile.illnessDescription),
            )
        }

        tips += PetTip(
            type = PetTipType.FOOD,
            titleRes = com.example.mypetshop.R.string.pet_tip_food_title,
            messageRes = com.example.mypetshop.R.string.pet_tip_food_body,
            formatArgs = listOf(profile.currentFood),
        )

        return tips
    }

    fun recommendProducts(profile: PetProfile, allProducts: List<ShopProduct>): List<ShopProduct> {
        val targetPetType = when (profile.petType) {
            PetType.DOG -> ProductPetType.DOG
            PetType.CAT -> ProductPetType.CAT
        }

        fun matchesPet(product: ShopProduct) =
            product.petType == targetPetType || product.petType == ProductPetType.BOTH

        val premiumForPet = allProducts.filter {
            it.category == ProductCategory.PREMIUM && matchesPet(it)
        }

        val prioritized = buildList {
            if (profile.wantsAdvanceTransition) {
                addAll(premiumForPet.filter { it.brand == "Advance" })
            }
            when {
                profile.petType == PetType.CAT && profile.isSterilized ->
                    addAll(premiumForPet.filter { it.name.contains("Esterilizado", ignoreCase = true) })
                profile.petType == PetType.DOG && profile.ageYears < 1 ->
                    addAll(premiumForPet.filter { it.name.contains("Cachorro", ignoreCase = true) || it.name.contains("Puppy", ignoreCase = true) })
                profile.petType == PetType.CAT && profile.ageYears < 1 ->
                    addAll(premiumForPet.filter { it.name.contains("Kitten", ignoreCase = true) || it.name.contains("Gato", ignoreCase = true) })
                profile.hasIllness ->
                    addAll(premiumForPet.filter { it.name.contains("Sensitive", ignoreCase = true) })
            }
            addAll(premiumForPet)
        }.distinctBy { it.id }

        return prioritized.take(3)
    }
}
