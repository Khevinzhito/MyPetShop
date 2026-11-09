package com.example.mypetshop.ui.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mypetshop.R
import com.example.mypetshop.domain.model.PetProfile
import com.example.mypetshop.domain.model.ProductCategory
import com.example.mypetshop.domain.model.ShopProduct
import com.example.mypetshop.ui.components.PetCompanionCard
import com.example.mypetshop.ui.components.ProductImage
import com.example.mypetshop.ui.theme.BackgroundWhite
import com.example.mypetshop.ui.theme.OrangeDark
import com.example.mypetshop.ui.theme.OrangeLight
import com.example.mypetshop.ui.theme.OrangePrimary
import com.example.mypetshop.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    profile: PetProfile? = null,
    onAdvanceClick: (() -> Unit)? = null,
    viewModel: ShopViewModel = hiltViewModel(),
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null) }
    val products by viewModel.products.collectAsState()

    val filtered = remember(searchQuery, selectedCategory, products) {
        products.filter { product ->
            val matchesSearch = searchQuery.isBlank() ||
                product.name.contains(searchQuery, ignoreCase = true) ||
                product.description.contains(searchQuery, ignoreCase = true) ||
                product.brand.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == null || product.category == selectedCategory
            matchesSearch && matchesCategory
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(OrangePrimary)
                .padding(16.dp),
        ) {
            Text(
                stringResource(R.string.marketplace_title),
                color = BackgroundWhite,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.marketplace_search_hint_food)) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = OrangePrimary) },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BackgroundWhite,
                    unfocusedContainerColor = BackgroundWhite,
                    focusedBorderColor = BackgroundWhite,
                    unfocusedBorderColor = BackgroundWhite,
                ),
            )
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text(stringResource(R.string.category_all)) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangeLight),
                )
                FilterChip(
                    selected = selectedCategory == ProductCategory.PREMIUM,
                    onClick = { selectedCategory = ProductCategory.PREMIUM },
                    label = { Text(stringResource(R.string.category_premium)) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangeLight),
                )
                FilterChip(
                    selected = selectedCategory == ProductCategory.STANDARD,
                    onClick = { selectedCategory = ProductCategory.STANDARD },
                    label = { Text(stringResource(R.string.category_standard)) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangeLight),
                )
                FilterChip(
                    selected = selectedCategory == ProductCategory.SNACK,
                    onClick = { selectedCategory = ProductCategory.SNACK },
                    label = { Text(stringResource(R.string.category_snack)) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangeLight),
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            if (profile != null && onAdvanceClick != null) {
                item(span = { GridItemSpan(2) }) {
                    PetCompanionCard(
                        profile = profile,
                        products = products,
                        onAdvanceClick = onAdvanceClick,
                        onAddToCart = viewModel::addToCart,
                    )
                }
            }
            items(filtered, key = { it.id }) { product ->
                ProductCard(product = product, onAddToCart = viewModel::addToCart)
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: ShopProduct,
    onAddToCart: (ShopProduct) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(Modifier.padding(10.dp)) {
            ProductImage(
                imageUri = product.imageUri,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                product.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = OrangeDark,
            )
            Text(
                "${product.brand} · ${categoryShortLabel(product.category)}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                formatPrice(product.priceCents),
                style = MaterialTheme.typography.titleMedium,
                color = OrangePrimary,
                fontWeight = FontWeight.Bold,
            )
            TextButton(
                onClick = { onAddToCart(product) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.AddShoppingCart, null, tint = OrangePrimary, modifier = Modifier.height(18.dp))
                Text(
                    stringResource(R.string.add_to_cart),
                    color = OrangePrimary,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun categoryShortLabel(category: ProductCategory): String = when (category) {
    ProductCategory.PREMIUM -> stringResource(R.string.category_premium)
    ProductCategory.STANDARD -> stringResource(R.string.category_standard)
    ProductCategory.SNACK -> stringResource(R.string.category_snack)
}

fun formatPrice(cents: Long): String {
    val value = cents / 100.0
    return "$ ${"%,.0f".format(value)}"
}
