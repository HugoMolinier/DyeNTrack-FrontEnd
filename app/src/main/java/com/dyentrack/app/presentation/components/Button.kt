package com.dyentrack.app.presentation.components

import androidx.compose.foundation.border
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.dyentrack.app.presentation.theme.addColor
import com.dyentrack.app.utils.pxToDp

enum class ButtonVariant { FILLED, OUTLINED }
enum class ButtonEmphasis { PRIMARY, SECONDARY }

@Composable
fun CustomButton(
    label: String,
    accessibilityLabel: String=label,
    onClick: (() -> Unit)? = null,
    variant: ButtonVariant = ButtonVariant.FILLED,
    emphasis: ButtonEmphasis = ButtonEmphasis.PRIMARY,
    modifier: Modifier = Modifier
) {
    // Couleurs selon l'emphasis
    val backgroundColor: Color
    val contentColor: Color

    when (emphasis) {
        ButtonEmphasis.PRIMARY -> {
            backgroundColor = MaterialTheme.colorScheme.primary // Bleu primaire
            contentColor = MaterialTheme.colorScheme.background
        }
        ButtonEmphasis.SECONDARY -> {
            backgroundColor = addColor.TertiaOne
            contentColor = MaterialTheme.colorScheme.primary
        }
    }

    val finalBackground = if (variant == ButtonVariant.OUTLINED) Color.Transparent else backgroundColor

    Button(
        onClick = { onClick?.invoke() },
        colors = ButtonDefaults.buttonColors(
            containerColor = finalBackground,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(5.dp),
        modifier = modifier
            .semantics { contentDescription = accessibilityLabel }
    ) {
        Text(text = label,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))

    }
}
