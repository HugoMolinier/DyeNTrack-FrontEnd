package com.dyentrack.app.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dyentrack.app.presentation.theme.TertiaOne

@Composable
fun InputCustom(
    modifier: Modifier= Modifier,
    text: MutableState<TextFieldValue>,
    placeholder:String = "",
    leadingIcon: (@Composable () -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text
    ){
    OutlinedTextField(
        singleLine = true,
        value = text.value,
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
                        },
        leadingIcon = leadingIcon,
        placeholder = { Text(placeholder) },
        modifier = modifier.fillMaxWidth().heightIn(min=44.dp,max=44.dp),
        textStyle = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),


        colors =  OutlinedTextFieldDefaults.colors(
            focusedContainerColor = TertiaOne,
            unfocusedContainerColor = TertiaOne,
            focusedBorderColor = Color(0x808CA3AF),
            unfocusedBorderColor = Color(0x808CA3AF),
            unfocusedPlaceholderColor =  Color(0x808CA3AF)

        ),
        shape = MaterialTheme.shapes.medium,

    )

}