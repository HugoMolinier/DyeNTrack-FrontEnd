package com.dyentrack.app.presentation.screens.parameters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun LegalNoticesPage(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopNavigationBar(
                stringResource(R.string.legal_title),
                leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onClickLeft = { navController.navigate("parametre") },
                rightSide = {
                    Icon(
                        Icons.Default.Close,
                        stringResource(R.string.back_to_menu),
                        modifier = Modifier.clickable { navController.navigate("home") }
                    )
                })

            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(48.dp)
            ) {

                // Section 1
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            stringResource(R.string.legal_heading_1),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                        )
                        Text(stringResource(R.string.legal_text_1_1),
                            style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor))
                    }
                }

                // Section 2
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            stringResource(R.string.legal_heading_2),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                        )
                        Text(stringResource(R.string.legal_text_2_1),
                            style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor))
                        Text(stringResource(R.string.legal_text_2_2),
                            style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor))
                    }
                }

                // Section 3
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            stringResource(R.string.legal_heading_3),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                        )
                        Text(stringResource(R.string.legal_text_3_1),
                            style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor))
                    }
                }

                // Section 4
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(bottom = 24.dp)) {
                        Text(
                            stringResource(R.string.legal_heading_4),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                        )
                        Text(stringResource(R.string.legal_text_4_1),
                            style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor))
                        Text(stringResource(R.string.legal_text_4_2),
                            style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor))
                        Text(stringResource(R.string.legal_text_4_3),
                            style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor))
                    }
                }
            }
        }
    }
}
