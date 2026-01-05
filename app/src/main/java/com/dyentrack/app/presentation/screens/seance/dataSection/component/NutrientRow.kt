package com.dyentrack.app.presentation.screens.seance.dataSection.component

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.enum.VariantType
import com.dyentrack.app.presentation.components.InputCustom
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun NutrientRow(
    label: String,
    valueState: MutableState<TextFieldValue>,
    unit: String = "g",
    isEditable: Boolean = true,
    onValueChange: (String) -> Unit = {},
    onFocusLost: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
    modifierInput: Modifier = Modifier,
    autoSync: Boolean
) {
    val context = LocalContext.current
    val toastText =  stringResource(R.string.not_editable)
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),

            ) {
            InputCustom(
                modifier = modifierInput
                    .width(80.dp - (unit.length * 10).dp)
                    .clickable(enabled = true, onClick = {
                        if (autoSync) {
                            Toast.makeText(
                                context,
                               toastText,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }),
                text = valueState,
                enabled = isEditable && !autoSync,
                placeholder = "0",
                replaceZeroToNull = true,
                onValueChange = onValueChange,
                onFocusLost = onFocusLost,
                keyboardType = KeyboardType.Decimal,
                variant = VariantType.EMPTY,
                singleLine = true
            )
            Text(
                unit,
                style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
            )
        }
    }
}