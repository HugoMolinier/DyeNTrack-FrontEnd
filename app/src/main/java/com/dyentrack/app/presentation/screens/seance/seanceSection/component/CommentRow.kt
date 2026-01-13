package com.dyentrack.app.presentation.screens.seance.seanceSection.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.InfoExerciseUser
import com.dyentrack.app.domain.model.preset.ExerciceInPreset
import com.dyentrack.app.presentation.theme.TertiaOne
import com.dyentrack.app.presentation.theme.secondaryTextColor
import com.dyentrack.app.presentation.utils.formatSubtitle


@Composable
fun CommentRow(
    infoExercise: InfoExerciseUser?,
    preset: ExerciceInPreset?,
    onClick: () -> Unit? = {},

    ) {

    val context = LocalContext.current
    Box(
        modifier = Modifier
            .padding(bottom = 8.dp, top = 4.dp)
            .shadow(
                elevation = 4.dp, shape = RoundedCornerShape(8.dp), clip = true
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
                        stringResource(R.string.note_label),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        infoExercise.note, style = MaterialTheme.typography.bodySmall.copy(
                            color = secondaryTextColor, fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
            if (hasNote && preset != null) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {


                    Text(
                        stringResource(R.string.range_label),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        preset.formatSubtitle(
                            repPlural = { count ->
                                context.resources.getQuantityString(
                                    R.plurals.rep, count, count
                                )
                            }), style = MaterialTheme.typography.bodySmall.copy(
                            color = secondaryTextColor, fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
            if (!hasNote && !hasRange) {
                Text(
                    stringResource(R.string.no_note_or_range_found),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = secondaryTextColor, fontWeight = FontWeight.Medium
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
                        stringResource(R.string.add_note),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                }
            }


        }
    }


}
