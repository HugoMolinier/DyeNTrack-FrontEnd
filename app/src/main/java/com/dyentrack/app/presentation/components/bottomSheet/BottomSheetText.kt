package com.dyentrack.app.presentation.components.bottomSheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun BottomSheetText(
    bottomSheetVisibility: Boolean,
    onClose: () -> Unit = {},
    title: String = "",
    text: String = "",
    styleText: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodySmall
) {
    BottomSheetContent(
        showBottomSheet = bottomSheetVisibility,
        onDismiss = onClose,
        closeButtonTitle = true,
        isfullScreen = false,
        title = title,
        childrenForExtend = {
            Column(
                modifier = Modifier.padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 20.dp,
                    bottom = 40.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = text,
                    style = styleText,
                    color = secondaryTextColor
                )
            }
        }
    )
}
