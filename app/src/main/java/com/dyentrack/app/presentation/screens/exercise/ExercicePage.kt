package com.dyentrack.app.presentation.screens.exercise

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dyentrack.app.domain.model.exercise.Exercise
import com.dyentrack.app.presentation.components.FavoriteIconButton
import com.dyentrack.app.presentation.components.InfoRow
import com.dyentrack.app.presentation.components.InputCustom
import com.dyentrack.app.presentation.components.InputVariant
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.components.YouTubePlayerViewComposable
import com.dyentrack.app.presentation.screens.exercise.component.SectionSelector
import com.dyentrack.app.presentation.theme.secondaryTextColor
import com.dyentrack.app.presentation.viewModel.AppViewModel

@Composable
fun ExercisePage (modifier: Modifier = Modifier,navController: NavController,exercise: Exercise,
                  appViewModel: AppViewModel ) {


    val idUser = 1.toLong()

    val appState by appViewModel.infoAllUser.collectAsState()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current


    Box(modifier = Modifier.fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
                keyboardController?.hide()
            })

    }) {
        Column {
            TopNavigationBar(
                "Détail de l’exercice", leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onClickLeft = { navController.popBackStack() })

            LazyColumn(modifier= modifier.fillMaxWidth().padding(horizontal = 20.dp).weight(1f), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                item {
                    Column {
                        Row(
                            modifier = modifier.fillMaxWidth()
                                .padding(top = 32.dp, end = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(exercise.nameFR)


                            FavoriteIconButton(
                                isFavorite = appState[exercise.idExercise]?.favorite?:false,
                                onClick = {
                                    val currentFavorite = appState[exercise.idExercise]?.favorite ?: false
                                    appViewModel.updateInfoExerciseUser(exercise.idExercise,!currentFavorite,null)
                                }
                            )
                        }
                        Text(
                         text = "Crée par " + when (exercise.idCreator) {
                             null -> "DyeNTrack"
                             idUser -> "Vous"
                             else -> "un utilisateur"
                         },
                            style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor))



                    }
                }


                item {SectionSelector()}

                if (exercise.description?.isNotBlank() == true){
                    item {Text(exercise.description, style = MaterialTheme.typography.bodyMedium)}
                }

                if (exercise.linkVideo?.isNotBlank() == true){
                    item { YouTubePlayerViewComposable(exercise.linkVideo) }

                }

                if (exercise.muscleInfos?.isNotEmpty() == true) {
                    val muscleUse = appViewModel.masterData?.muscles
                        ?.filter { it.id in exercise.muscleInfos.map { it -> it.idMuscle } }
                        ?.map { it.nameFR } ?: emptySet()

                    item {Column(modifier = modifier.fillMaxWidth()) {
                        Text("Détails",modifier= Modifier.padding(vertical = 12.dp), style = MaterialTheme.typography.bodyLarge)
                        InfoRow(
                            label = "Aussi Appellé",
                            value = "future nom anglais",
                        )
                        InfoRow(
                            label = "Muscle utilisé",
                            value = muscleUse.joinToString(", "),
                            border = true
                        )
                        InfoRow(
                            label = "Muscle Principal",
                            value =  exercise.muscleInfos
                                .firstOrNull { it.principal }
                                ?.idMuscle
                                ?.let { id -> appViewModel.masterData?.muscles?.firstOrNull { it.id == id }?.nameFR }
                                ?: "Aucun",
                        )
                        InfoRow(
                            label = "Equipement possible",
                            value = appViewModel.masterData?.equipment?.joinToString(", ") { it.nameFR }
                                ?: "Aucun",
                        border = true
                            )

                        InfoRow(
                            label = "Exécution possible",
                            value = appViewModel.masterData?.lateralites?.joinToString(", ") { it.nameFR }
                                ?: "Aucune",
                        )


                    }


                    }
                    item{
                        val noteState = remember { mutableStateOf(TextFieldValue(appState[exercise.idExercise]?.note ?: "")) }


                        InputCustom(modifier = Modifier.padding(top=12.dp).height(70.dp), noteState, placeholder = "Ajouter une note", variant = InputVariant.OUTLINED,
                            singleLine = false,
                            onFocusLost = { finalText ->
                                appViewModel.updateInfoExerciseUser(exercise.idExercise, null,finalText)
                            }
                        )


                    }

                }



            }
        }
    }
}