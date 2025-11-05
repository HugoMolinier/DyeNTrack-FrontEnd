package com.dyentrack.app.presentation.components
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dyentrack.app.presentation.theme.PrimaryTextColor
import com.dyentrack.app.presentation.theme.TertiaOne
import com.dyentrack.app.presentation.theme.secondaryTextColor

enum class InputVariant { FILLED, OUTLINED }

@Composable
fun InputCustom(
    modifier: Modifier = Modifier,
    text: MutableState<TextFieldValue>,
    placeholder: String = "",
    onValueChange: ((String) -> Unit)? = null,
    onFocusLost: ((String) -> Unit)? = null,
    enabled: Boolean = true,
    leadingIcon:   ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    variant : InputVariant ?= InputVariant.FILLED,
    singleLine:Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }

    Row (
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(color = TertiaOne, shape = RoundedCornerShape(if (variant == InputVariant.OUTLINED) 12.dp else 5.dp))

            .then(
                if (variant == InputVariant.OUTLINED) Modifier.border(
                    width = 1.dp,
                    color = Color(0xFF9CA3AF).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                ) else Modifier
            ).padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = secondaryTextColor.copy(alpha = 0.5f)
            )
        }
        BasicTextField(
            value = text.value,
            enabled= enabled,
            modifier=Modifier.weight(1f).onFocusChanged { focusState ->
                if (isFocused && !focusState.isFocused) {
                    onFocusLost?.invoke(text.value.text)
                }
                isFocused = focusState.isFocused
            },

            onValueChange = { newValue ->
                val filteredText = when (keyboardType) {
                    KeyboardType.Number -> newValue.text.filter { it.isDigit() }
                    KeyboardType.Decimal -> newValue.text.filter { it.isDigit() || (it == '.' && newValue.text.count { c -> c == '.' } <= 1) }
                    else -> newValue.text
                }
                val newCursor = minOf(filteredText.length, newValue.selection.end)
                text.value = TextFieldValue(
                    text = filteredText,
                    selection = androidx.compose.ui.text.TextRange(newCursor)
                )
                onValueChange?.invoke(filteredText)
            },
            singleLine = singleLine,
            textStyle = MaterialTheme.typography.bodySmall.copy(
                color = PrimaryTextColor

            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = if(singleLine) Alignment.CenterStart else Alignment.TopStart
                ) {
                    if (text.value.text.isEmpty()) {
                        Text(
                            text = placeholder,

                            style = MaterialTheme.typography.bodySmall.copy(
                                color = secondaryTextColor.copy(alpha = 0.5f),

                                textAlign = TextAlign.Center
                            ),
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}
