package com.ezi.gallery.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ezi.gallery.R
import com.ezi.gallery.core.model.GridDensity
import com.ezi.gallery.core.model.SortOrder
import com.ezi.gallery.core.model.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val gridDensity by viewModel.gridDensity.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val groupByDate by viewModel.groupByDate.collectAsState()

    var showThemeDialog by remember { mutableStateOf(false) }
    var showDensityDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsCategoryHeader("Appearance")

            SettingsClickableRow(
                title = "Theme",
                subtitle = themeMode.title,
                onClick = { showThemeDialog = true }
            )

            SettingsClickableRow(
                title = "Default Grid Density",
                subtitle = gridDensity.label,
                onClick = { showDensityDialog = true }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            SettingsCategoryHeader("Library & Organization")

            SettingsClickableRow(
                title = "Default Sort Order",
                subtitle = sortOrder.title,
                onClick = { showSortDialog = true }
            )

            SettingsSwitchRow(
                title = "Group Photos by Date",
                subtitle = "Display headers for Today, Yesterday, and dates",
                checked = groupByDate,
                onCheckedChange = { viewModel.setGroupByDate(it) }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            SettingsCategoryHeader("About")

            SettingsInfoRow(
                title = "App Name",
                value = "Ezi Gallery"
            )

            SettingsInfoRow(
                title = "Version",
                value = "1.0.0 (Phase 1)"
            )

            SettingsInfoRow(
                title = "Privacy",
                value = "100% Offline • No tracking • No accounts"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Theme Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Choose Theme") },
            text = {
                Column {
                    ThemeMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (mode == themeMode),
                                onClick = {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = mode.title, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Grid Density Dialog
    if (showDensityDialog) {
        AlertDialog(
            onDismissRequest = { showDensityDialog = false },
            title = { Text("Grid Density") },
            text = {
                Column {
                    GridDensity.entries.forEach { density ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setGridDensity(density)
                                    showDensityDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (density == gridDensity),
                                onClick = {
                                    viewModel.setGridDensity(density)
                                    showDensityDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = density.label, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDensityDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Sort Order Dialog
    if (showSortDialog) {
        AlertDialog(
            onDismissRequest = { showSortDialog = false },
            title = { Text("Sort Order") },
            text = {
                Column {
                    SortOrder.entries.forEach { order ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setSortOrder(order)
                                    showSortDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (order == sortOrder),
                                onClick = {
                                    viewModel.setSortOrder(order)
                                    showSortDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = order.title, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSortDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun SettingsCategoryHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 6.dp)
    )
}

@Composable
fun SettingsClickableRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SettingsInfoRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
