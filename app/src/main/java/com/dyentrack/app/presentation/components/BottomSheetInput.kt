package com.dyentrack.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.enum.VariantType
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun BottomSheetInput(
    bottomSheetVisibility: Boolean,
    onNew: (String) -> Unit,
    onClose: () -> Unit= {},
    title:String="",
    buttonText:String="",
    inputDefaultValue:String?=null,

    ) {
    val inputState = remember(inputDefaultValue) {
        mutableStateOf(TextFieldValue(inputDefaultValue ?: "", TextRange(inputDefaultValue?.length?:0)))
    }
    var isError by remember { mutableStateOf(false) }

    BottomSheetContent(
        showBottomSheet = bottomSheetVisibility,
        onDismiss = { onClose()},
        isfullScreen = false,
        dragZoneComponent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 4.dp, bottom = 4.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(text = title, style = MaterialTheme.typography.titleSmall)
                Button(
                    onClick = {  if (inputState.value.text.isBlank()) {
                        isError = true
                    } else {
                        isError = false
                        onNew(inputState.value.text)
                    } },
                    modifier = Modifier.padding(bottom = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) { Text(buttonText, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(horizontal = 8.dp)) }


            }
        },
        childrenForExtend = {
            Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 20.dp, bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InputCustom(text = inputState, autoFocus = true, maxCaracter = 80, placeholder = "Nom du preset", variant = VariantType.OUTLINED)

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    if (isError) {
                        Icon(
                            Icons.Outlined.ErrorOutline,
                            "Error",
                            tint = Color.Red,
                            modifier = Modifier.size(14.dp)
                        )


                    }
                    Text(
                        text = if (isError)"Le nom ne peut etre vide" else "",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    )
}