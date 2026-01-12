package com.dyentrack.app.presentation.screens.seance.seanceSection.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.InfoExerciseUser
import com.dyentrack.app.domain.model.LANGUAGE
import com.dyentrack.app.domain.model.MesureUnit
import com.dyentrack.app.domain.model.SetType
import com.dyentrack.app.domain.model.preset.ExerciceInPreset
import com.dyentrack.app.domain.model.preset.Preset
import com.dyentrack.app.presentation.screens.seance.SeanceUiState
import com.dyentrack.app.presentation.screens.seance.SeanceViewModel
import com.dyentrack.app.presentation.theme.TertiaOne
import com.dyentrack.app.presentation.theme.addColor
import com.dyentrack.app.presentation.theme.secondaryTextColor
import com.dyentrack.app.presentation.utils.convertWeight
import com.dyentrack.app.presentation.utils.formatSubtitle
import com.dyentrack.app.presentation.utils.toAbbreviatedDate
import java.time.LocalDate
import java.time.temporal.ChronoUnit


@Composable
fun CommentRow(
    infoExercise: InfoExerciseUser?,
    preset: ExerciceInPreset?,
    onClick:() -> Unit?={},

) {

    val context = LocalContext.current
    Box(
        modifier = Modifier
            .padding( bottom = 8.dp, top = 4.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp),
                clip = true
            )
            .background(TertiaOne, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .padding(end = 20.dp, bottom = 12.dp, top = 12.dp),
    ) {


        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)


        ) {
            val hasNote = infoExercise?.note?.isNotBlank() == true
            val hasRange = preset?.rangeRepInf != null || preset?.rangeRepSup != null

            if (hasNote) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {


                    Text(
                        "Note :",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        infoExercise.note,
                        style = MaterialTheme.typography.bodySmall
                            .copy(
                                color = secondaryTextColor,
                                fontWeight = FontWeight.Medium
                            )
                    )
                }
            }
            if (hasNote && preset!=null) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {


                    Text(
                        "Fourchette :",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        preset.formatSubtitle(
                            repPlural = { count ->
                                context.resources.getQuantityString(
                                    R.plurals.rep,
                                    count,
                                    count
                                )
                            }
                        ),
                        style = MaterialTheme.typography.bodySmall
                            .copy(
                                color = secondaryTextColor,
                                fontWeight = FontWeight.Medium
                            )
                    )
                }
            }
            if (!hasNote && !hasRange) {
                if (!hasNote && !hasRange) {
                    Text(
                        "Aucune note ou fourchette (lié à un preset) trouvée",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = secondaryTextColor,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(4.dp)
                    )

                    Button(
                        onClick = {
                            onClick.invoke()
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            "Ajouter une note",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }



        }
    }


}
