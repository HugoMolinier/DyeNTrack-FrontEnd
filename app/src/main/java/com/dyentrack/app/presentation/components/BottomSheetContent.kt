package com.dyentrack.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.platform.LocalConfiguration
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetContent(
    title: String,
    closeButtonTitle: Boolean = true,
    childrenForExtend: @Composable (() -> Unit)? = null,
    footerComponent: @Composable (() -> Unit)? = null,
    scrollable: Boolean = true,
    showBottomSheet: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (!showBottomSheet) return
    val colorScheme = MaterialTheme.colorScheme
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )


    ModalBottomSheet(
        onDismissRequest = { onDismiss?.invoke() },
        sheetState = sheetState,
                modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()        // hauteur auto selon le contenu
,
        containerColor = colorScheme.background,
        shape = MaterialTheme.shapes.large
        ,

        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()

                    .padding(top = 20.dp, bottom = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(3.dp)
                        .background(Color(0xFF4B5563))
                        .align(Alignment.Center)
                )
            }
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.85f)
        ) {
            if (title.isNotBlank() || closeButtonTitle) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 4.dp, bottom = 4.dp,end=4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = title ?: "", style = MaterialTheme.typography.titleSmall)

                    if (closeButtonTitle) {
                        IconButton(onClick = { onDismiss?.invoke() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fermer"
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFF292929), thickness = 1.dp)
            }

            // --- CONTENU SCROLLABLE ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (scrollable) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        childrenForExtend?.invoke()
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        childrenForExtend?.invoke()
                    }
                }
            }


            footerComponent?.let {
                HorizontalDivider(color = Color(0xFF292929), thickness = 1.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorScheme.background)
                ) {
                    it()
                }
            }
        }
    }
}
