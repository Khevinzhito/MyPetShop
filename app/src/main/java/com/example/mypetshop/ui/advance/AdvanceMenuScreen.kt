package com.example.mypetshop.ui.advance

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mypetshop.R
import com.example.mypetshop.domain.model.PetProfile
import com.example.mypetshop.ui.components.AppTopBar
import com.example.mypetshop.ui.theme.BackgroundWhite
import com.example.mypetshop.ui.theme.OrangeDark
import com.example.mypetshop.ui.theme.OrangeLight
import com.example.mypetshop.ui.theme.OrangePrimary
import com.example.mypetshop.ui.theme.TextSecondary

@Composable
fun AdvanceMenuScreen(
    profile: PetProfile,
    onAboutUsClick: () -> Unit,
    embedInScaffold: Boolean = true,
    viewModel: AdvanceMenuViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(profile) { viewModel.loadForProfile(profile) }

    val body: @Composable (Modifier) -> Unit = { modifier ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedButton(
                onClick = onAboutUsClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary),
            ) {
                Text(stringResource(R.string.about_us_button))
            }
            LogusSection()
            RecipesSection(profile, uiState.recipes, viewModel::onRecipeClick)
            FamilyStorySection()
        }
    }

    if (embedInScaffold) {
        Scaffold(
            containerColor = BackgroundWhite,
            topBar = { AppTopBar(title = stringResource(R.string.advance_menu_title)) },
        ) { padding -> body(Modifier.padding(padding)) }
    } else {
        body(Modifier)
    }

    uiState.selectedRecipe?.let { recipe ->
        if (!uiState.showRecipeDialog) return@let
        AlertDialog(
            onDismissRequest = viewModel::onDismissRecipeDialog,
            title = { Text(recipe.name) },
            text = {
                Column {
                    Image(
                        painter = painterResource(R.drawable.img_advance_product),
                        contentDescription = recipe.imageDescription,
                        modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(stringResource(R.string.recipe_dialog_characteristics), fontWeight = FontWeight.Bold, color = OrangeDark)
                    recipe.characteristics.forEach { Text("• $it") }
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.recipe_dialog_suitable), fontWeight = FontWeight.Bold, color = OrangeDark)
                    Text(recipe.suitableFor)
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::onDismissRecipeDialog) {
                    Text(stringResource(R.string.recipe_dialog_close))
                }
            },
        )
    }
}

@Composable
private fun LogusSection() {
    SectionCard(Icons.Filled.Storefront, stringResource(R.string.about_logus_title)) {
        Image(
            painter = painterResource(R.drawable.logo_logus),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(80.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(Modifier.height(12.dp))
        Text(stringResource(R.string.about_logus_body), color = TextSecondary)
    }
}

@Composable
private fun RecipesSection(
    profile: PetProfile,
    recipes: List<com.example.mypetshop.domain.model.AdvanceRecipe>,
    onRecipeClick: (com.example.mypetshop.domain.model.AdvanceRecipe) -> Unit,
) {
    SectionCard(Icons.Filled.Pets, stringResource(R.string.advance_recipes_title)) {
        Text("${stringResource(R.string.advance_recipes_subtitle)} (${profile.breed})", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(12.dp))
        recipes.forEach { recipe ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = OrangeLight.copy(alpha = 0.35f)),
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(recipe.name, style = MaterialTheme.typography.titleMedium, color = OrangeDark)
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = { onRecipeClick(recipe) }, colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary)) {
                        Text(stringResource(R.string.view_recipe_button))
                    }
                }
            }
        }
    }
}

@Composable
private fun FamilyStorySection() {
    SectionCard(Icons.Filled.FamilyRestroom, stringResource(R.string.family_story_title)) {
        Text(stringResource(R.string.family_story_subtitle), color = OrangeDark, style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Image(
                painter = painterResource(R.drawable.img_family_border_collie),
                contentDescription = stringResource(R.string.family_story_dog_name),
                modifier = Modifier.size(72.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
            )
            Column {
                Text(stringResource(R.string.family_story_dog_name), color = OrangeDark, style = MaterialTheme.typography.titleMedium)
                Text(stringResource(R.string.family_story_couple), color = TextSecondary)
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(stringResource(R.string.family_story_body), color = TextSecondary)
    }
}

@Composable
private fun SectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = OrangePrimary)
                Spacer(Modifier.size(12.dp))
                Text(title, style = MaterialTheme.typography.titleLarge, color = OrangeDark)
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}
