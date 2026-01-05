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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dyentrack.app.domain.model.enum.SizeType
import com.dyentrack.app.domain.model.enum.VariantType
import com.dyentrack.app.presentation.theme.TertiaOne
import com.dyentrack.app.presentation.theme.secondaryTextColor

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
    variant : VariantType ?= VariantType.FILLED,
    singleLine:Boolean = true,
    replaceZeroToNull:Boolean=false,
    size:  SizeType= SizeType.LARGE,
    maxCaracter: Int? = null,
    hideContent: Boolean = false,
    centerText: Boolean = false,
    autoFocus: Boolean = false,
) {
    var isFocused by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(!hideContent) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(autoFocus) {
        if (autoFocus) {
            focusRequester.requestFocus()
        }
    }

    Row (
        modifier = modifier
            .fillMaxWidth()
            .height(if (variant == VariantType.EMPTY)32.dp else 44.dp)
            .background(color = TertiaOne, shape = RoundedCornerShape(if (variant == VariantType.OUTLINED) 12.dp else 5.dp))

            .then(
                if (variant == VariantType.OUTLINED) Modifier.border(
                    width = 1.dp,
                    color = Color(0xFF9CA3AF).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                ) else Modifier
            ).padding(
                start = when (size) {
                    SizeType.LARGE -> 16.dp
                    SizeType.MEDIUM -> 8.dp
                    SizeType.NO -> 0.dp
                    SizeType.SMALL ->4.dp
                },
                end = when (size) {
                    SizeType.LARGE -> if (hideContent) 8.dp else 16.dp
                    SizeType.MEDIUM -> 8.dp
                    SizeType.NO -> 0.dp
                    SizeType.SMALL ->4.dp
                },
                top = if (singleLine) 0.dp else 10.dp,
                bottom = if (singleLine) 0.dp else 10.dp
            ),
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
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType,
                imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    onFocusLost?.invoke(text.value.text) // ← commit la valeur quand Done est pressé
                }
            ),
            visualTransformation = if (contentVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier=Modifier.weight(1f).focusRequester(focusRequester).onFocusChanged { focusState ->
                // FOCUS OUT
                if (isFocused && !focusState.isFocused) {
                    onFocusLost?.invoke(text.value.text)
                }
                isFocused = focusState.isFocused
            },

            onValueChange = { newValue ->

                var filteredText = when (keyboardType) {
                    KeyboardType.Number -> newValue.text.filter { it.isDigit() }
                    KeyboardType.Decimal -> newValue.text
                        .filter { it.isDigit() || it == '.' || it == ',' }
                        .let { text ->
                            val normalizedText = text.replace(',', '.')
                            val parts = normalizedText.split('.')
                            if (parts.size > 1) {
                                parts[0] + "." + parts[1].take(1)
                            } else {
                                normalizedText
                            }
                        }
                    else -> newValue.text
                }


                if (maxCaracter != null && filteredText.length > maxCaracter) {
                    filteredText = if (keyboardType == KeyboardType.Decimal && !filteredText.contains('.')) {
                        filteredText.takeLast(maxCaracter - 1) // -1 pour le futur point + chiffre
                    } else {
                        filteredText.takeLast(maxCaracter)
                    }
                }

                if (filteredText != text.value.text) {
                    // Calcul de la nouvelle position du curseur
                    val newCursor = minOf(newValue.selection.end, filteredText.length)
                    text.value = TextFieldValue(
                        text = filteredText,
                        selection = TextFieldValue(filteredText, TextRange(newCursor)).selection
                    )
                    onValueChange?.invoke(filteredText)
                } else {
                    // Si le texte ne change pas mais que l'utilisateur clique, on garde la sélection
                    if (text.value.selection != newValue.selection) {
                        text.value = text.value.copy(selection = newValue.selection)
                    }
                }
            },
            singleLine = singleLine,
            textStyle = MaterialTheme.typography.bodySmall.copy(
                color = if (enabled) MaterialTheme .colorScheme.onSurface else secondaryTextColor.copy(alpha= 0.8f),
                textAlign = if (centerText) TextAlign.Center else if (variant == VariantType.EMPTY) TextAlign.End else TextAlign.Start

            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = if (singleLine) if (centerText) Alignment.Center else if (variant == VariantType.EMPTY) Alignment.CenterEnd else Alignment.CenterStart else Alignment.TopStart

                ) {
                    if (text.value.text.isEmpty()) {
                        Text(
                            text = placeholder,

                            style = MaterialTheme.typography.bodySmall.copy(
                                color = secondaryTextColor.copy(alpha = 0.5f),

                                textAlign = if (variant == VariantType.EMPTY) TextAlign.End else TextAlign.Start
                            ),
                        )
                    }
                    innerTextField()
                    if (hideContent) { // Affiche bouton pour basculer visibilité
                        IconButton(onClick = { contentVisible = !contentVisible },            modifier = Modifier.align(Alignment.CenterEnd).size(22.dp)) {
                            Icon(
                                imageVector = if (contentVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                contentDescription = if (contentVisible) "Masquer" else "Afficher",
                                tint = secondaryTextColor.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        )
    }
}
