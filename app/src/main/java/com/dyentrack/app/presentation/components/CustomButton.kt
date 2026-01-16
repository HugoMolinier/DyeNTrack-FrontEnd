package com.dyentrack.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dyentrack.app.domain.model.enums.EmphasisType
import com.dyentrack.app.domain.model.enums.SizeType
import com.dyentrack.app.domain.model.enums.VariantType
import com.dyentrack.app.presentation.theme.AddColor


@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    label: String,
    accessibilityLabel: String=label,
    onClick: (() -> Unit)? = null,
    variant: VariantType = VariantType.FILLED,
    emphasis: EmphasisType = EmphasisType.PRIMARY,
    enable: Boolean = true,
    size:SizeType =SizeType.LARGE,
    isLoading:Boolean =false,
    contentPadding:PaddingValues?= PaddingValues(0.dp)
) {

    val backgroundColor: Color
    val contentColor: Color

    val contentPadding = contentPadding?:when (size) {
        SizeType.SMALL -> PaddingValues(
            vertical = 2.dp
        )
        SizeType.NO -> PaddingValues(
            vertical = 0.dp
        )
        SizeType.MEDIUM -> PaddingValues(
            vertical = 4.dp
        )
        SizeType.LARGE -> PaddingValues(
            vertical = 8.dp
        )
    }

    when (emphasis) {
        EmphasisType.PRIMARY -> {
            backgroundColor = MaterialTheme.colorScheme.primary
            contentColor = MaterialTheme.colorScheme.background
        }
        EmphasisType.SECONDARY -> {
            backgroundColor = AddColor.TertiaOne
            contentColor = MaterialTheme.colorScheme.primary
        }
        EmphasisType.TERTIA -> {
            backgroundColor = AddColor.TertiaOne
            contentColor = MaterialTheme.colorScheme.onSurface
        }
        EmphasisType.DESTRUCTIVE -> {
            backgroundColor = Color.Red
            contentColor = Color.Red
        }
    }

    val finalBackground = if (variant == VariantType.OUTLINED) Color.Transparent else backgroundColor
    if (variant == VariantType.OUTLINED) {
        OutlinedButton(
            onClick = { onClick?.invoke() },
            enabled = enable,
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(0.dp),
            border = BorderStroke(1.dp, contentColor),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = finalBackground,
                contentColor = contentColor
            ),
            modifier = modifier.semantics { contentDescription = accessibilityLabel } .alpha(if (enable) 1f else 0.8f)
        ) {
            Row(modifier=Modifier.padding(contentPadding),verticalAlignment = Alignment.CenterVertically,  horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally) ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSurface,
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .size(12.dp)
                    )
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(contentPadding)
                )
            }
        }
    } else {
        Button(
            onClick = { onClick?.invoke() },
            colors = ButtonDefaults.buttonColors(
                containerColor = finalBackground,
                contentColor = contentColor
            ),
            contentPadding = PaddingValues(0.dp),
            enabled = enable,
            shape = RoundedCornerShape(5.dp),
            modifier = modifier.semantics { contentDescription = accessibilityLabel } .alpha(if (enable) 1f else 0.8f)
        ) {
            Row(modifier=Modifier.padding(contentPadding),verticalAlignment = Alignment.CenterVertically ,  horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSurface,
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .size(12.dp)

                    )
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
            }
        }
    }

}
