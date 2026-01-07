package com.dyentrack.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun CenterMenu(
    title: String? = null,
    descrition:String?=null,
    items: List<MenuItemData>?=emptyList(),
    onDismiss: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = { onDismiss?.invoke() }),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .clickable(enabled = false) {},
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = MaterialTheme .shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                title?.let {
                    Text(
                        text = it,
                        style =MaterialTheme.typography.bodyLarge
                    )
                }
                descrition?.let{
                    Text(
                        text = it,
                        style =MaterialTheme.typography.bodySmall,
                        color = secondaryTextColor
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items?.forEachIndexed { index, item ->
                        Row(    horizontalArrangement = Arrangement.Center,  modifier = Modifier     .weight(1f).fillMaxWidth().then(
                                if (item.canBeClick){
                                    Modifier.clickable { item.onClick.invoke() }
                                }
                                else Modifier
                                )) {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 12.dp),
                                textAlign = TextAlign.Center,
                                color = item.color
                            )
                        }


                    }
                }
            }
        }
    }
}
