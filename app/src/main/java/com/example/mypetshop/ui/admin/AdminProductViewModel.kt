package com.example.mypetshop.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypetshop.domain.model.ProductCategory
import com.example.mypetshop.domain.model.ProductPetType
import com.example.mypetshop.domain.model.ShopProduct
import com.example.mypetshop.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ProductFormState(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val priceText: String = "",
    val brand: String = "",
    val category: ProductCategory = ProductCategory.PREMIUM,
    val petType: ProductPetType = ProductPetType.BOTH,
    val imageUri: String? = null,
    val isActive: Boolean = true,
    val isVisible: Boolean = false,
    val errorKey: String? = null,
)

@HiltViewModel
class AdminProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {

    val products: StateFlow<List<ShopProduct>> = productRepository
        .observeAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _formState = MutableStateFlow(ProductFormState())
    val formState: StateFlow<ProductFormState> = _formState.asStateFlow()

    init {
        viewModelScope.launch { productRepository.seedIfEmpty() }
    }

    fun openCreateForm() {
        _formState.value = ProductFormState(isVisible = true)
    }

    fun openEditForm(product: ShopProduct) {
        _formState.value = ProductFormState(
            id = product.id,
            name = product.name,
            description = product.description,
            priceText = (product.priceCents / 100).toString(),
            brand = product.brand,
            category = product.category,
            petType = product.petType,
            imageUri = product.imageUri,
            isActive = product.isActive,
            isVisible = true,
        )
    }

    fun closeForm() {
        _formState.value = ProductFormState()
    }

    fun onNameChange(value: String) = updateForm { it.copy(name = value, errorKey = null) }
    fun onDescriptionChange(value: String) = updateForm { it.copy(description = value, errorKey = null) }
    fun onPriceChange(value: String) = updateForm { it.copy(priceText = value.filter { c -> c.isDigit() }, errorKey = null) }
    fun onBrandChange(value: String) = updateForm { it.copy(brand = value, errorKey = null) }
    fun onCategoryChange(value: ProductCategory) = updateForm { it.copy(category = value) }
    fun onPetTypeChange(value: ProductPetType) = updateForm { it.copy(petType = value) }
    fun onImageUriChange(uri: String?) = updateForm { it.copy(imageUri = uri) }
    fun onActiveChange(value: Boolean) = updateForm { it.copy(isActive = value) }

    fun saveProduct() {
        val form = _formState.value
        val priceCents = form.priceText.toLongOrNull()?.times(100)
        when {
            form.name.isBlank() -> _formState.update { it.copy(errorKey = "error_product_name") }
            form.description.isBlank() -> _formState.update { it.copy(errorKey = "error_product_description") }
            form.brand.isBlank() -> _formState.update { it.copy(errorKey = "error_product_brand") }
            priceCents == null || priceCents <= 0 -> _formState.update { it.copy(errorKey = "error_product_price") }
            else -> viewModelScope.launch {
                productRepository.upsert(
                    ShopProduct(
                        id = form.id.ifBlank { "prod-${UUID.randomUUID()}" },
                        name = form.name.trim(),
                        description = form.description.trim(),
                        priceCents = priceCents,
                        brand = form.brand.trim(),
                        category = form.category,
                        petType = form.petType,
                        imageUri = form.imageUri,
                        isActive = form.isActive,
                    ),
                )
                closeForm()
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch { productRepository.delete(productId) }
    }

    private fun updateForm(block: (ProductFormState) -> ProductFormState) {
        _formState.update { block(it) }
    }
}
