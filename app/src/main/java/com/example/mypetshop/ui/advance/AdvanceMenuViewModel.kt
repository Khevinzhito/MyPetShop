package com.example.mypetshop.ui.advance

import androidx.lifecycle.ViewModel
import com.example.mypetshop.data.seed.AdvanceCatalog
import com.example.mypetshop.domain.model.AdvanceRecipe
import com.example.mypetshop.domain.model.PetProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class AdvanceMenuUiState(
    val recipes: List<AdvanceRecipe> = emptyList(),
    val selectedRecipe: AdvanceRecipe? = null,
    val showRecipeDialog: Boolean = false,
)

@HiltViewModel
class AdvanceMenuViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AdvanceMenuUiState())
    val uiState: StateFlow<AdvanceMenuUiState> = _uiState.asStateFlow()

    fun loadForProfile(profile: PetProfile) {
        _uiState.update { it.copy(recipes = AdvanceCatalog.recipesFor(profile.petType)) }
    }

    fun onRecipeClick(recipe: AdvanceRecipe) {
        _uiState.update { it.copy(selectedRecipe = recipe, showRecipeDialog = true) }
    }

    fun onDismissRecipeDialog() {
        _uiState.update { it.copy(showRecipeDialog = false, selectedRecipe = null) }
    }
}
