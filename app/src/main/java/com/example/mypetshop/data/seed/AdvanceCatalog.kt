package com.example.mypetshop.data.seed

import com.example.mypetshop.domain.model.AdvanceRecipe
import com.example.mypetshop.domain.model.PetType

object AdvanceCatalog {

    val dogBreeds = listOf(
        "Border Collie", "Labrador Retriever", "Golden Retriever",
        "Pastor Alemán", "Bulldog Francés", "Chihuahua", "Mestizo", "Otra raza",
    )

    val catBreeds = listOf(
        "Persa", "Siamés", "Maine Coon", "Bengalí",
        "British Shorthair", "Mestizo", "Otra raza",
    )

    private val dogRecipes = listOf(
        AdvanceRecipe("advance_dog_adult", "Advance Adult", PetType.DOG,
            "Bolsa Advance Adult para perros adultos",
            listOf("Proteína de alta calidad", "Omega 3 y 6", "Prebióticos digestivos"),
            "Perros adultos de todas las razas"),
        AdvanceRecipe("advance_dog_puppy", "Advance Puppy", PetType.DOG,
            "Bolsa Advance Puppy para cachorros",
            listOf("DHA para desarrollo cerebral", "Calcio y fósforo", "Defensas naturales"),
            "Cachorros hasta 12 meses"),
        AdvanceRecipe("advance_dog_senior", "Advance Senior", PetType.DOG,
            "Bolsa Advance Senior",
            listOf("Antioxidantes", "Condroprotectores", "Digestión fácil"),
            "Perros senior a partir de 7 años"),
        AdvanceRecipe("advance_dog_sensitive", "Advance Sensitive", PetType.DOG,
            "Advance Sensitive salmón y arroz",
            listOf("Fórmula hipoalergénica", "Piel sensible", "Sin colorantes artificiales"),
            "Perros con sensibilidad digestiva o cutánea"),
        AdvanceRecipe("advance_dog_light", "Advance Light", PetType.DOG,
            "Advance Light control de peso",
            listOf("Menor contenido calórico", "L-carnitina", "Mantiene masa muscular"),
            "Perros con sobrepeso o esterilizados"),
        AdvanceRecipe("advance_dog_veterinary", "Advance Veterinary Diets", PetType.DOG,
            "Línea Advance Veterinary Diets",
            listOf("Dietas clínicas", "Apoyo renal y articular", "Nutrición terapéutica Logus"),
            "Perros con patologías específicas"),
    )

    private val catRecipes = listOf(
        AdvanceRecipe("advance_cat_adult", "Advance Adult Gato", PetType.CAT,
            "Bolsa Advance Adult gato",
            listOf("Control del bolus urinario", "Proteína felina", "Pelaje brillante"),
            "Gatos adultos"),
        AdvanceRecipe("advance_cat_kitten", "Advance Kitten", PetType.CAT,
            "Bolsa Advance Kitten",
            listOf("DHA cognitivo", "Minerales óseos", "Alta energía"),
            "Gatitos hasta 12 meses"),
        AdvanceRecipe("advance_cat_sterilized", "Advance Sterilized", PetType.CAT,
            "Bolsa Advance Sterilized",
            listOf("Control de peso", "Prevención urinaria", "Saciedad prolongada"),
            "Gatos esterilizados"),
        AdvanceRecipe("advance_cat_sensitive", "Advance Sensitive Gato", PetType.CAT,
            "Advance Sensitive gato",
            listOf("Salmón principal", "Digestión delicada", "Piel sensible"),
            "Gatos con estómago sensible"),
        AdvanceRecipe("advance_cat_hairball", "Advance Hairball", PetType.CAT,
            "Advance Hairball",
            listOf("Fibra anti bolas de pelo", "Malt digestivo", "Pelaje largo"),
            "Gatos con bolas de pelo"),
        AdvanceRecipe("advance_cat_veterinary", "Advance Veterinary Diets Gato", PetType.CAT,
            "Advance Veterinary Diets gato",
            listOf("Soporte renal", "Fórmulas clínicas", "Pauta veterinaria"),
            "Gatos con necesidades clínicas"),
    )

    fun recipesFor(petType: PetType): List<AdvanceRecipe> = when (petType) {
        PetType.DOG -> dogRecipes
        PetType.CAT -> catRecipes
    }

    fun breedsFor(petType: PetType): List<String> = when (petType) {
        PetType.DOG -> dogBreeds
        PetType.CAT -> catBreeds
    }
}
