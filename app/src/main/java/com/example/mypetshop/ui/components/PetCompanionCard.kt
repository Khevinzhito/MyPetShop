package com.example.mypetshop.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mypetshop.R
import com.example.mypetshop.data.tips.PetTip
import com.example.mypetshop.data.tips.PetTipsEngine
import com.example.mypetshop.domain.model.PetProfile
import com.example.mypetshop.domain.model.PetType
import com.example.mypetshop.domain.model.ShopProduct
import com.example.mypetshop.ui.shop.formatPrice
import com.example.mypetshop.ui.theme.BackgroundWhite
import com.example.mypetshop.ui.theme.OrangeDark
import com.example.mypetshop.ui.theme.OrangeLight
import com.example.mypetshop.ui.theme.OrangePrimary
import com.example.mypetshop.ui.theme.TextSecondary

@Composable
fun PetCompanionCard(
    profile: PetProfile,
    products: List<ShopProduct>,
    onAdvanceClick: () -> Unit,
    onAddToCart: (ShopProduct) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val tips = remember(profile) { PetTipsEngine.buildTips(profile) }
    val recommendations = remember(profile, products) { PetTipsEngine.recommendProducts(profile, products) }
    var tipIndex by remember(profile) { mutableIntStateOf(0) }
    val currentTip = tips.getOrNull(tipIndex) ?: return

    val mascotRes = if (profile.petType == PetType.DOG) R.drawable.ic_mascot_dog else R.drawable.ic_mascot_cat
    val greetingRes = if (profile.petType == PetType.DOG) {
        R.string.pet_companion_greeting_dog
    } else {
        R.string.pet_companion_greeting_cat
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeLight.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(Modifier.padding(if (compact) 12.dp else 16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Image(
                    painter = painterResource(mascotRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(if (compact) 56.dp else 72.dp)
                        .clip(RoundedCornerShape(12.dp)),
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        stringResource(greetingRes, profile.breed),
                        style = MaterialTheme.typography.titleSmall,
                        color = OrangeDark,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        stringResource(R.string.pet_companion_subtitle, profile.ageYears),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                    )
                    if (!compact) {
                        Spacer(Modifier.height(8.dp))
                        TipBubble(tip = currentTip)
                    }
                }
            }
            if (compact) {
                Spacer(Modifier.height(8.dp))
                TipBubble(tip = currentTip)
            }
            if (tips.size > 1) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    tips.forEachIndexed { index, _ ->
                        FilterChip(
                            selected = tipIndex == index,
                            onClick = { tipIndex = index },
                            label = { Text("${index + 1}", style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = OrangePrimary,
                                selectedLabelColor = BackgroundWhite,
                            ),
                        )
                    }
                }
            }
            if (recommendations.isNotEmpty() && !compact) {
                Spacer(Modifier.height(12.dp))
                Text(
                    stringResource(R.string.pet_tip_recommendations_title),
                    style = MaterialTheme.typography.labelMedium,
                    color = OrangeDark,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(6.dp))
                recommendations.forEach { product ->
                    RecommendationRow(product = product, onAddToCart = onAddToCart)
                }
            }
            if (!compact) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TextButton(onClick = onAdvanceClick, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Pets, null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                        Text(
                            stringResource(R.string.pet_tip_see_advance),
                            color = OrangePrimary,
                            modifier = Modifier.padding(start = 4.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TipBubble(tip: PetTip) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.Lightbulb, null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
            Column(Modifier.padding(start = 8.dp)) {
                Text(
                    stringResource(tip.titleRes),
                    style = MaterialTheme.typography.labelMedium,
                    color = OrangeDark,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = formatTipMessage(tip),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
        }
    }
}

@Composable
private fun RecommendationRow(
    product: ShopProduct,
    onAddToCart: (ShopProduct) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(BackgroundWhite)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(product.name, style = MaterialTheme.typography.bodySmall, color = OrangeDark, fontWeight = FontWeight.Medium)
            Text("${product.brand} · ${formatPrice(product.priceCents)}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
        Button(
            onClick = { onAddToCart(product) },
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary, contentColor = BackgroundWhite),
            contentPadding = ButtonDefaults.ContentPadding,
        ) {
            Icon(Icons.Default.AddShoppingCart, null, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun formatTipMessage(tip: PetTip): String {
    if (tip.formatArgs.isEmpty()) return stringResource(tip.messageRes)
    // Importante: pasar los args con sus tipos originales (Int, Long, Double, String...)
    // para que los especificadores como %d / %f de las plantillas no fallen con
    // IllegalFormatConversionException al renderizar.
    val args = tip.formatArgs.toTypedArray()
    return stringResource(tip.messageRes, *args)
}

@Composable
fun PetCompanionMini(profile: PetProfile, modifier: Modifier = Modifier) {
    val tips = remember(profile) { PetTipsEngine.buildTips(profile) }
    val tip = tips.firstOrNull() ?: return
    val mascotRes = if (profile.petType == PetType.DOG) R.drawable.ic_mascot_dog else R.drawable.ic_mascot_cat

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(mascotRes),
            contentDescription = null,
            modifier = Modifier.size(40.dp),
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                stringResource(tip.titleRes),
                style = MaterialTheme.typography.labelSmall,
                color = OrangePrimary,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                formatTipMessage(tip),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                maxLines = 2,
            )
        }
    }
}
