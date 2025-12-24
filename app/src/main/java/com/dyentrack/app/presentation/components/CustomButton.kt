package com.dyentrack.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dyentrack.app.presentation.components.type.*
import com.dyentrack.app.presentation.theme.addColor


@Composable
fun CustomButton(
    label: String,
    accessibilityLabel: String=label,
    onClick: (() -> Unit)? = null,
    variant: VariantType = VariantType.FILLED,
    emphasis: EmphasisType = EmphasisType.PRIMARY,
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    size:SizeType =SizeType.LARGE
) {
    // Couleurs selon l'emphasis
    val backgroundColor: Color
    val contentColor: Color

    val contentPadding = when (size) {
        SizeType.SMALL -> PaddingValues(
            horizontal = 12.dp,
            vertical = 2.dp
        )
        SizeType.MEDIUM -> PaddingValues(
            horizontal = 16.dp,
            vertical = 4.dp
        )
        SizeType.LARGE -> PaddingValues(
            horizontal = 20.dp,
            vertical = 8.dp
        )
    }

    when (emphasis) {
        EmphasisType.PRIMARY -> {
            backgroundColor = MaterialTheme.colorScheme.primary // Bleu primaire
            contentColor = MaterialTheme.colorScheme.background
        }
        EmphasisType.SECONDARY -> {
            backgroundColor = addColor.TertiaOne
            contentColor = MaterialTheme.colorScheme.primary
        }
    }

    val finalBackground = if (variant == VariantType.OUTLINED) Color.Transparent else backgroundColor
    if (variant == VariantType.OUTLINED) {
        OutlinedButton(
            onClick = { onClick?.invoke() },
            enabled = enable,
            shape = RoundedCornerShape(5.dp),
            border = BorderStroke(1.dp, contentColor),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = finalBackground,
                contentColor = contentColor
            ),
            modifier = modifier.semantics { contentDescription = accessibilityLabel }
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(contentPadding)
            )
        }
    } else {
        Button(
            onClick = { onClick?.invoke() },
            colors = ButtonDefaults.buttonColors(
                containerColor = finalBackground,
                contentColor = contentColor
            ),
            enabled = enable,
            shape = RoundedCornerShape(5.dp),
            modifier = modifier.semantics { contentDescription = accessibilityLabel }
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(contentPadding)
            )
        }
    }

}
