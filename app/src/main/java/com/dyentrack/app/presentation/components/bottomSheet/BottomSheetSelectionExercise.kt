package com.dyentrack.app.presentation.components.bottomSheet

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.domain.mapper.toSearchKey
import com.dyentrack.app.domain.model.InfoExerciseUser
import com.dyentrack.app.domain.model.LANGUAGE
import com.dyentrack.app.domain.model.exercise.Exercise
import com.dyentrack.app.domain.model.enum.ExerciseTypeFilter
import com.dyentrack.app.domain.model.getName
import com.dyentrack.app.domain.model.enum.EmphasisType
import com.dyentrack.app.domain.model.enum.VariantType
import com.dyentrack.app.presentation.components.CardWithContent
import com.dyentrack.app.presentation.components.ChipDropDown
import com.dyentrack.app.presentation.components.FavoriteIconButton
import com.dyentrack.app.presentation.components.InputCustom
import com.dyentrack.app.presentation.components.LeftIcon
import com.dyentrack.app.presentation.components.MenuItemData
import com.dyentrack.app.presentation.theme.secondaryTextColor
import com.dyentrack.app.presentation.utils.ExerciseImages

@Composable
fun BottomSheetSelectionExercise(
    bottomSheetVisibility: Boolean,
    selectedExerciseIds: List<Long>?=emptyList(),
    officialExercise: List<Exercise>?,
    infoMapUser :Map<Long, InfoExerciseUser>,
    titleAction: (@Composable () -> Unit)? = null,
    navController: NavController,
    onClose: () -> Unit= {},
    onClickExercise: (Exercise) -> Unit= {},
    onClickFavorite: (Long) -> Unit= {},
    onResetFilter: () -> Unit= {},
    onUpdateQuery:(String) ->Unit= {},
    musclesGroupMap:Map<Long, String>?=emptyMap(),
    title:String="",
    userlanguage: LANGUAGE,
    footerChildren: @Composable () -> Unit = {},

    ) {
    val context = LocalContext.current
    val muscleGroupsDefault = stringResource(R.string.filter_muscle_groups)
    var searchQuery by remember { mutableStateOf("") }
    var typeFilter by remember { mutableStateOf(ExerciseTypeFilter.ALL) }
    var groupFilter by remember { mutableStateOf(muscleGroupsDefault)}
    val filteredExercises = remember(
        searchQuery,
        typeFilter,
        groupFilter,
        officialExercise,
        userlanguage
    ) {
        officialExercise?.filter { exercise ->

            val matchesSearch =  searchQuery.isBlank() || when(userlanguage){
                LANGUAGE.FR -> exercise.toSearchKey().contains(searchQuery)
                LANGUAGE.EN -> exercise.nameEN.contains(searchQuery, ignoreCase = true)
            }

            val typeSearch = when (typeFilter) {
                ExerciseTypeFilter.OFFICIAL -> exercise.idCreator == null
                ExerciseTypeFilter.FAVORITE -> infoMapUser[exercise.idExercise]?.favorite ?: false
                ExerciseTypeFilter.USER_CREATED -> exercise.idCreator != null
                else -> true
            }

            val groupFilter =
                groupFilter == muscleGroupsDefault || musclesGroupMap?.get (exercise.mainFocusGroup ) == groupFilter


            matchesSearch && typeSearch && groupFilter
        }
    }
    val nombreFiltresActifs = listOf(
        typeFilter to ExerciseTypeFilter.ALL,
        groupFilter to muscleGroupsDefault
    ).count { it.first != it.second } + if (searchQuery.isNotEmpty()) 1 else 0

    BottomSheetContent(
        title = title,
        titleAction = titleAction,
        showBottomSheet = bottomSheetVisibility,
        onDismiss = { onClose() },
        childrenForExtend = {
            InputCustom(
                text = remember { mutableStateOf(TextFieldValue(searchQuery)) },
                onValueChange = { newValue ->
                    searchQuery = newValue
                    onUpdateQuery(newValue)
                },
                placeholder = stringResource(R.string.filter_search),
                leadingIcon = Icons.Default.Search,
                modifier = Modifier.padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 20.dp,
                    bottom = 12.dp
                ),
                variant = VariantType.OUTLINED
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
                                listOfSelectableItem = ExerciseTypeFilter.entries.map {
                                    it.toDisplayString(
                                        context
                                    )
                                },
                                defaultSelected = typeFilter.toDisplayString(context),
                                emphasys = EmphasisType.SECONDARY,
                                onSelected = { selectedString ->
                                    typeFilter = ExerciseTypeFilter.entries.first {
                                        it.toDisplayString(context) == selectedString
                                    }
                                })
                            ChipDropDown(
                                listOfSelectableItem = listOf(muscleGroupsDefault) + musclesGroupMap?.values.orEmpty(),
                                defaultSelected = groupFilter,
                                emphasys = EmphasisType.SECONDARY,
                                onSelected = { groupFilter = it })
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .clickable(enabled = nombreFiltresActifs > 0) {
                                        typeFilter = ExerciseTypeFilter.ALL
                                        groupFilter = muscleGroupsDefault
                                        onResetFilter()
                                    }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.filter_clear),
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    stringResource(R.string.filter_clear) + " (${nombreFiltresActifs})",
                                    style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
                                )
                            }
                            Text(
                                "${filteredExercises?.count()}/${officialExercise?.count()}",
                                style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
                            )
                        }

                    }
                }


                //exercise
                items(filteredExercises ?: listOf(), key = { it.idExercise }) { exercise ->
                    val isSelected = selectedExerciseIds?.contains(exercise.idExercise) == true
                    Box(Modifier.padding(12.dp, 4.dp)) {
                        CardWithContent(
                            modifier = Modifier.padding(10.dp, 4.dp),
                            exercise.getName(userlanguage),
                            subText = musclesGroupMap?.get(exercise.mainFocusGroup)?.uppercase()
                                ?: "NaN",
                            leftIcon = LeftIcon.Single(
                                ExerciseImages.map[exercise.idExercise]
                                    ?: R.drawable.logotransparent
                            ),
                            componentRight = {
                                FavoriteIconButton(
                                    isFavorite = infoMapUser[exercise.idExercise]?.favorite
                                        ?: false,
                                    onClick = { onClickFavorite(exercise.idExercise) }
                                )
                            },
                            menuOnLongClick = listOf(
                                MenuItemData(
                                    stringResource(R.string.exercise_view),
                                    {
                                        navController.navigate("exercise/${exercise.idExercise}")
                                    }

                                )
                            ),
                            subTextEmphasis = EmphasisType.PRIMARY,
                            onClick = { onClickExercise(exercise) },
                            badgeText = if (isSelected) (selectedExerciseIds.indexOf(exercise.idExercise) + 1).toString() else null
                        )
                    }
                }
            }
        },
        footerComponent = {
            footerChildren()

        }
    )
}