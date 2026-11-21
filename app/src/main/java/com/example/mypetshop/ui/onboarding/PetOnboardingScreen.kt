package com.example.mypetshop.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mypetshop.R
import com.example.mypetshop.domain.model.PetGender
import com.example.mypetshop.domain.model.PetProfile
import com.example.mypetshop.domain.model.PetType
import com.example.mypetshop.ui.components.AppTopBar
import com.example.mypetshop.ui.theme.BackgroundWhite
import com.example.mypetshop.ui.theme.ErrorRed
import com.example.mypetshop.ui.theme.OrangeDark
import com.example.mypetshop.ui.theme.OrangeLight
import com.example.mypetshop.ui.theme.OrangePrimary
import com.example.mypetshop.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PetOnboardingScreen(
    onProfileSaved: (PetProfile) -> Unit,
    onSkip: (() -> Unit)? = null,
    embedInScaffold: Boolean = true,
    viewModel: PetOnboardingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = OrangePrimary,
        unfocusedBorderColor = OrangeLight,
        focusedLabelColor = OrangeDark,
    )

    LaunchedEffect(uiState.savedProfile) {
        uiState.savedProfile?.let { profile ->
            viewModel.consumeSavedProfile()
            onProfileSaved(profile)
        }
    }

    val content: @Composable (Modifier) -> Unit = { modifier ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text(stringResource(R.string.onboarding_subtitle), color = TextSecondary)
                    Spacer(Modifier.height(20.dp))

                    Text(stringResource(R.string.pet_type_label), color = OrangeDark)
                    Spacer(Modifier.height(8.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PetType.entries.forEach { type ->
                            FilterChip(
                                selected = uiState.petType == type,
                                onClick = { viewModel.onPetTypeSelected(type) },
                                label = { Text(stringResource(if (type == PetType.DOG) R.string.pet_type_dog else R.string.pet_type_cat)) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangeLight, selectedLabelColor = OrangeDark),
                            )
                        }
                    }
                    uiState.petTypeErrorKey?.let { OnboardingError(it) }

                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = uiState.ageText,
                        onValueChange = viewModel::onAgeChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.pet_age_label)) },
                        placeholder = { Text(stringResource(R.string.pet_age_hint)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = uiState.ageErrorKey != null,
                        supportingText = uiState.ageErrorKey?.let { { OnboardingError(it) } },
                        colors = fieldColors,
                    )
                    Spacer(Modifier.height(12.dp))
                    ToggleRow(stringResource(R.string.pet_sterilized_label), uiState.isSterilized, viewModel::onSterilizedChange)
                    Spacer(Modifier.height(12.dp))
                    ToggleRow(stringResource(R.string.pet_illness_label), uiState.hasIllness, viewModel::onHasIllnessChange)
                    if (uiState.hasIllness) {
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = uiState.illnessDescription,
                            onValueChange = viewModel::onIllnessDescriptionChange,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.pet_illness_hint)) },
                            isError = uiState.illnessErrorKey != null,
                            supportingText = uiState.illnessErrorKey?.let { { OnboardingError(it) } },
                            colors = fieldColors,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.currentFood,
                        onValueChange = viewModel::onCurrentFoodChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.pet_food_label)) },
                        placeholder = { Text(stringResource(R.string.pet_food_hint)) },
                        isError = uiState.currentFoodErrorKey != null,
                        supportingText = uiState.currentFoodErrorKey?.let { { OnboardingError(it) } },
                        colors = fieldColors,
                    )
                    Spacer(Modifier.height(12.dp))

                    if (uiState.petType != null) {
                        ExposedDropdownMenuBox(
                            expanded = uiState.breedExpanded,
                            onExpandedChange = viewModel::onBreedExpandedChange,
                        ) {
                            OutlinedTextField(
                                value = uiState.breed,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                label = { Text(stringResource(R.string.pet_breed_label)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(uiState.breedExpanded) },
                                isError = uiState.breedErrorKey != null,
                                supportingText = uiState.breedErrorKey?.let { { OnboardingError(it) } },
                                colors = fieldColors,
                            )
                            ExposedDropdownMenu(
                                expanded = uiState.breedExpanded,
                                onDismissRequest = { viewModel.onBreedExpandedChange(false) },
                            ) {
                                uiState.availableBreeds.forEach { breed ->
                                    DropdownMenuItem(
                                        text = { Text(breed) },
                                        onClick = { viewModel.onBreedChange(breed) },
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    Text(stringResource(R.string.pet_gender_label), color = OrangeDark)
                    Spacer(Modifier.height(8.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PetGender.entries.forEach { gender ->
                            FilterChip(
                                selected = uiState.gender == gender,
                                onClick = { viewModel.onGenderSelected(gender) },
                                label = { Text(stringResource(if (gender == PetGender.MALE) R.string.pet_gender_male else R.string.pet_gender_female)) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangeLight, selectedLabelColor = OrangeDark),
                            )
                        }
                    }
                    uiState.genderErrorKey?.let { OnboardingError(it) }

                    Spacer(Modifier.height(16.dp))
                    Text(stringResource(R.string.advance_transition_label), style = MaterialTheme.typography.titleSmall, color = OrangeDark)
                    Text(stringResource(R.string.advance_transition_hint), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Spacer(Modifier.height(8.dp))
                    ToggleRow(
                        if (uiState.wantsAdvanceTransition) stringResource(R.string.yes) else stringResource(R.string.no),
                        uiState.wantsAdvanceTransition,
                        viewModel::onAdvanceTransitionChange,
                    )
                    Spacer(Modifier.height(24.dp))

                    uiState.saveErrorKey?.let {
                        Text(
                            text = stringResource(R.string.error_save_profile),
                            color = ErrorRed,
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    Button(
                        onClick = viewModel::onContinue,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving,
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary, contentColor = BackgroundWhite),
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(color = BackgroundWhite, modifier = Modifier.height(20.dp))
                        } else {
                            Text(stringResource(R.string.onboarding_continue))
                        }
                    }
                    onSkip?.let { skip ->
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = skip,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary),
                        ) {
                            Text(stringResource(R.string.skip_pet_onboarding))
                        }
                    }
                }
            }
        }
    }

    if (embedInScaffold) {
        Scaffold(
            containerColor = BackgroundWhite,
            topBar = { AppTopBar(title = stringResource(R.string.onboarding_title)) },
        ) { padding -> content(Modifier.padding(padding)) }
    } else {
        content(Modifier)
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f), color = OrangeDark)
        Switch(checked, onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = BackgroundWhite, checkedTrackColor = OrangePrimary))
    }
}

@Composable
private fun OnboardingError(key: String) {
    val res = when (key) {
        "error_pet_type" -> R.string.error_pet_type
        "error_age_empty" -> R.string.error_age_empty
        "error_age_invalid" -> R.string.error_age_invalid
        "error_breed" -> R.string.error_breed
        "error_gender" -> R.string.error_gender
        "error_current_food" -> R.string.error_current_food
        "error_illness_description" -> R.string.error_illness_description
        else -> R.string.error_pet_type
    }
    Text(stringResource(res), color = ErrorRed, style = MaterialTheme.typography.bodySmall)
}
