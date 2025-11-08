package com.dyentrack.app.presentation.screens.seance.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.presentation.components.BottomSheetContent
import com.dyentrack.app.presentation.components.ButtonEmphasis
import com.dyentrack.app.presentation.components.CardWithContent
import com.dyentrack.app.presentation.components.ChipDropDown
import com.dyentrack.app.presentation.components.ChipsEmphasys
import com.dyentrack.app.presentation.components.CustomButton
import com.dyentrack.app.presentation.components.FavoriteIconButton
import com.dyentrack.app.presentation.components.InputCustom
import com.dyentrack.app.presentation.components.InputVariant
import com.dyentrack.app.presentation.components.SubTextEmphasis
import com.dyentrack.app.presentation.screens.seance.SeanceUiState
import com.dyentrack.app.presentation.screens.seance.SeanceViewModel
import com.dyentrack.app.presentation.theme.secondaryTextColor
import com.dyentrack.app.presentation.utils.ExerciseImages

@Composable
fun BottomSheetSelectionExercise(
    state: SeanceUiState,
    viewModel: SeanceViewModel,
    navController: NavController
) {
    var typeFilter by remember { mutableStateOf("Tous") }
    var groupFilter by remember { mutableStateOf("Groupes Musculaires") }
    val infoMapState by viewModel.infoExerciseUserFlow.collectAsState()
    val filteredExercises = remember(
        state.searchQuery,
        typeFilter,
        groupFilter,
        viewModel.masterData?.officialExercise
    ) {
        viewModel.masterData?.officialExercise?.filter { exercise ->
            val matchesSearch = exercise.nameFR.contains(state.searchQuery, ignoreCase = true)

            val typeSearch = when (typeFilter) {
                "Officiel" -> exercise.idCreator == null
                "Favorie" -> infoMapState[exercise.idExercise]?.favorite ?: false
                "Non Officiel" -> exercise.idCreator != null
                else -> true
            }

            val groupFilter =
                groupFilter == "Groupes Musculaires" || viewModel.masterData?.musclesGroup
                    ?.firstOrNull { it.id == exercise.mainFocusGroup }?.nameFR == groupFilter


            matchesSearch && typeSearch && groupFilter
        }
    }
    val nombreFiltresActifs = listOf(
        typeFilter to "Tous",
        groupFilter to "Groupes Musculaires"
    ).count { it.first != it.second } + if (state.searchQuery.isNotEmpty()) 1 else 0

    BottomSheetContent(
        title = "Ajouter un exercice",
        showBottomSheet = state.isBottomSheetVisible,
        onDismiss = { viewModel.toggleBottomSheet(false) },
        childrenForExtend = {
            InputCustom(
                text = remember { mutableStateOf(TextFieldValue(state.searchQuery)) },
                onValueChange = { newValue ->
                    viewModel.updateSearchQuery(newValue)
                },
                placeholder = "Search",
                leadingIcon = Icons.Default.Search,
                modifier = Modifier.padding(12.dp, 20.dp),
                variant = InputVariant.OUTLINED
            )



            LazyColumn(modifier = Modifier.fillMaxSize()) {

                //filtre
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp, bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            ChipDropDown(
                                listOfSelectableItem = listOf(
                                    "Tous",
                                    "Officiel",
                                    "Favorie",
                                    "Non Officiel"
                                ),
                                defaultSelected = typeFilter,
                                emphasys = ChipsEmphasys.SECONDARY
                            ) { typeFilter = it }
                            ChipDropDown(
                                listOfSelectableItem = listOf("Groupes Musculaires") +
                                        viewModel.masterData?.musclesGroup?.map { it.nameFR }
                                            .orEmpty(),
                                defaultSelected = groupFilter,
                                emphasys = ChipsEmphasys.SECONDARY
                            ) { groupFilter = it }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .clickable(enabled = nombreFiltresActifs > 0) {
                                        typeFilter = "Tous"
                                        groupFilter = "Groupes Musculaires"
                                        viewModel.updateSearchQuery("")
                                    }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Effacer les filtres",
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "Effacer les filtres (${nombreFiltresActifs})",
                                    style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
                                )
                            }
                            Text(
                                "${filteredExercises?.count()}/${viewModel.masterData?.officialExercise?.count()}",
                                style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
                            )
                        }

                    }
                }


                //exercise
                items(filteredExercises ?: listOf()) { exercise ->
                    val isSelected = exercise.idExercise in state.exerciseToAdd
                    CardWithContent(
                        modifier = Modifier.padding(12.dp, 4.dp),
                        exercise.nameFR,
                        subText = viewModel.masterData?.musclesGroup
                            ?.firstOrNull { it.id == exercise.mainFocusGroup }?.nameFR?.uppercase()
                            ?: "NaN",

                        leftIcon = ExerciseImages.map[exercise.idExercise]?:   R.drawable.logotransparent,
                        componentRight = {
                            FavoriteIconButton(
                                isFavorite = infoMapState[exercise.idExercise]?.favorite ?: false,
                                onClick = {
                                    val currentFavorite =
                                        infoMapState[exercise.idExercise]?.favorite ?: false
                                    viewModel.updateInfoExerciseUser(
                                        exercise.idExercise,
                                        !currentFavorite,
                                        null
                                    )
                                }
                            )
                        },
                        menuOnLongClick = mapOf(
                            "Voir Exercice" to {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "exercise",
                                    exercise
                                )
                                navController.navigate("exercise")
                            }
                        ),
                        subTextEmphasis = SubTextEmphasis.PRIMARY,
                        onClick = { viewModel.toggleExerciseSelection(exercise.idExercise) },
                        badgeText = if (isSelected) (state.exerciseToAdd.indexOf(exercise.idExercise) + 1).toString() else null
                    )
                }
            }
        },
        footerComponent = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 20.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CustomButton(
                    label = "Annuler",
                    emphasis = ButtonEmphasis.SECONDARY,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.toggleBottomSheet(false)
                    }
                )
                CustomButton(
                    label = "Ajouter",
                    modifier = Modifier.weight(1f),
                    enable = (state.exerciseToAdd.isNotEmpty()),
                    onClick = {
                        viewModel.addExerciseToSeance()
                    }
                )
            }

        }
    )
}