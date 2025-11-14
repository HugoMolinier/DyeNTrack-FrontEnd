package com.dyentrack.app.presentation.screens.seance.dataSection.component

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.dyentrack.app.presentation.components.InputCustom
import com.dyentrack.app.presentation.components.InputVariant
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
    modifierInput:Modifier = Modifier
) {
    Row(modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label,style=MaterialTheme.typography.bodySmall)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            InputCustom(
                modifier = modifierInput.width(80.dp - (unit.length * 10).dp),
                text = valueState,
                enabled = isEditable,
                placeholder = "0",
                onValueChange = onValueChange,
                onFocusLost = onFocusLost,
                keyboardType = KeyboardType.Decimal,
                variant = InputVariant.EMPTY,
                singleLine = true
            )
            Text(
                unit,
                style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
            )
        }
    }
}