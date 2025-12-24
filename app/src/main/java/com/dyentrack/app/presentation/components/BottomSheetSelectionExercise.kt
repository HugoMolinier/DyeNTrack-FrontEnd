package com.dyentrack.app.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.InfoExerciseUser
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.presentation.components.type.EmphasisType
import com.dyentrack.app.presentation.theme.secondaryTextColor
import com.dyentrack.app.presentation.utils.ExerciseImages

@Composable
fun BottomSheetSelectionExercise(
    bottomSheetVisibility: Boolean,
    selectedExerciseIds: List<Long>,
    masterDataStatic: MasterDataStatic?,
    infoMapUser :Map<Long, InfoExerciseUser>,
    navController: NavController,
    onClose: () -> Unit= {},
    onClickExercise: (Long) -> Unit= {},
    onClickFavorite: (Long) -> Unit= {},
    onResetFilter: () -> Unit= {},
    onUpdateQuery:(String) ->Unit= {},
    footerChildren: @Composable () -> Unit = {}

) {
    var searchQuery by remember { mutableStateOf("") }
    var typeFilter by remember { mutableStateOf("Tous") }
    var groupFilter by remember { mutableStateOf("Groupes Musculaires") }
    val filteredExercises = remember(
        searchQuery,
        typeFilter,
        groupFilter,
        masterDataStatic?.officialExercise
    ) {
        masterDataStatic?.officialExercise?.filter { exercise ->
            val matchesSearch = exercise.nameFR.contains(searchQuery, ignoreCase = true)

            val typeSearch = when (typeFilter) {
                "Officiel" -> exercise.idCreator == null
                "Favorie" -> infoMapUser[exercise.idExercise]?.favorite ?: false
                "Non Officiel" -> exercise.idCreator != null
                else -> true
            }

            val groupFilter =
                groupFilter == "Groupes Musculaires" || masterDataStatic.musclesGroup
                    .firstOrNull { it.id == exercise.mainFocusGroup }?.nameFR == groupFilter


            matchesSearch && typeSearch && groupFilter
        }
    }
    val nombreFiltresActifs = listOf(
        typeFilter to "Tous",
        groupFilter to "Groupes Musculaires"
    ).count { it.first != it.second } + if (searchQuery.isNotEmpty()) 1 else 0

    BottomSheetContent(
        title = "Ajouter un exercice",
        showBottomSheet = bottomSheetVisibility,
        onDismiss = { onClose()},
        childrenForExtend = {
            InputCustom(
                text = remember { mutableStateOf(TextFieldValue(searchQuery)) },
                onValueChange = { newValue ->
                    searchQuery = newValue
                    onUpdateQuery(newValue)
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
                                emphasys = EmphasisType.SECONDARY
                            ) { typeFilter = it }
                            ChipDropDown(
                                listOfSelectableItem = listOf("Groupes Musculaires") +
                                        masterDataStatic?.musclesGroup?.map { it.nameFR }
                                            .orEmpty(),
                                defaultSelected = groupFilter,
                                emphasys = EmphasisType.SECONDARY
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
                                        onResetFilter()
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
                                "${filteredExercises?.count()}/${masterDataStatic?.officialExercise?.count()}",
                                style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
                            )
                        }

                    }
                }


                //exercise
                items(filteredExercises ?: listOf()) { exercise ->
                    val isSelected = exercise.idExercise in selectedExerciseIds
                    Box(Modifier.padding(12.dp, 4.dp)){
                    CardWithContent(
                        modifier = Modifier.padding(10.dp, 4.dp),
                        exercise.nameFR,
                        subText = masterDataStatic?.musclesGroup
                            ?.firstOrNull { it.id == exercise.mainFocusGroup }?.nameFR?.uppercase()
                            ?: "NaN",

                        leftIcon = LeftIcon.Single(ExerciseImages.map[exercise.idExercise]?:   R.drawable.logotransparent),
                        componentRight = {
                            FavoriteIconButton(
                                isFavorite = infoMapUser[exercise.idExercise]?.favorite ?: false,
                                onClick = { onClickFavorite(exercise.idExercise) }
                            )
                        },
                        menuOnLongClick = listOf(
                                MenuItemData(
                                    "Voir Exercice",
                                    {
                                        navController.currentBackStackEntry?.savedStateHandle?.set(
                                            "exercise",
                                            exercise
                                        )
                                        navController.navigate("exercise")
                                    }

                                )
                            ),
                        subTextEmphasis = EmphasisType.PRIMARY,
                        onClick = { onClickExercise(exercise.idExercise) },
                        badgeText = if (isSelected) (selectedExerciseIds.indexOf(exercise.idExercise) + 1).toString() else null
                    )}
                }
            }
        },
        footerComponent = {
            footerChildren()

        }
    )
}