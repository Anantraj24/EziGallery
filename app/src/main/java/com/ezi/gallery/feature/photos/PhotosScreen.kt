package com.ezi.gallery.feature.photos

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ezi.gallery.EziGalleryApp
import com.ezi.gallery.R
import com.ezi.gallery.core.model.GridDensity
import com.ezi.gallery.core.model.MediaItem
import com.ezi.gallery.core.model.SortOrder
import com.ezi.gallery.core.permissions.PermissionHelper
import com.ezi.gallery.feature.common.DateHeaderItem
import com.ezi.gallery.feature.common.EmptyStateView
import com.ezi.gallery.feature.common.LoadingStateView
import com.ezi.gallery.feature.common.MediaThumbnail
import com.ezi.gallery.feature.common.PermissionRequestView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen(
    viewModel: PhotosViewModel,
    onNavigateToViewer: (Long) -> Unit,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val uiState by viewModel.uiState.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Launcher for requesting media permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        if (granted) {
            viewModel.checkPermissionAndStart()
        }
    }

    // Launcher for Android 11+ MediaStore Delete IntentSender
    val deleteIntentSenderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.clearSelection()
            viewModel.refresh()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (uiState.isSelectionMode) {
                // Selection action bar
                TopAppBar(
                    title = {
                        Text(
                            text = "${uiState.selectedIds.size} selected",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close selection")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.selectAll() }) {
                            Icon(imageVector = Icons.Default.SelectAll, contentDescription = "Select all")
                        }
                        IconButton(onClick = {
                            val selected = viewModel.getSelectedItems()
                            if (selected.isNotEmpty()) {
                                val shareIntent = (context.applicationContext as EziGalleryApp).repository.createShareIntent(selected)
                                context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Media"))
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                        }
                        IconButton(onClick = { viewModel.toggleFavoriteSelected() }) {
                            Icon(imageVector = Icons.Default.Favorite, contentDescription = "Favorite")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            } else {
                // Normal top app bar
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    actions = {
                        IconButton(onClick = onNavigateToSearch) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        }
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Options")
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Grid: 2 Columns") },
                                    onClick = {
                                        viewModel.setGridDensity(GridDensity.TWO)
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Grid: 3 Columns") },
                                    onClick = {
                                        viewModel.setGridDensity(GridDensity.THREE)
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Grid: 4 Columns") },
                                    onClick = {
                                        viewModel.setGridDensity(GridDensity.FOUR)
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Grid: 5 Columns") },
                                    onClick = {
                                        viewModel.setGridDensity(GridDensity.FIVE)
                                        showMenu = false
                                    }
                                )
                                androidx.compose.material3.HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("Sort: Newest first") },
                                    onClick = {
                                        viewModel.setSortOrder(SortOrder.DATE_DESC)
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Sort: Oldest first") },
                                    onClick = {
                                        viewModel.setSortOrder(SortOrder.DATE_ASC)
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Sort: Name (A-Z)") },
                                    onClick = {
                                        viewModel.setSortOrder(SortOrder.NAME_ASC)
                                        showMenu = false
                                    }
                                )
                                androidx.compose.material3.HorizontalDivider()
                                DropdownMenuItem(
                                    text = {
                                        Text(if (uiState.groupByDate) "Disable date grouping" else "Enable date grouping")
                                    },
                                    onClick = {
                                        viewModel.setGroupByDate(!uiState.groupByDate)
                                        showMenu = false
                                    }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                !uiState.hasPermission -> {
                    PermissionRequestView(
                        onRequestPermission = {
                            permissionLauncher.launch(PermissionHelper.getRequiredPermissions())
                        },
                        onOpenSettings = {
                            PermissionHelper.openAppSettings(context)
                        }
                    )
                }
                uiState.isLoading -> {
                    LoadingStateView()
                }
                uiState.mediaList.isEmpty() -> {
                    EmptyStateView(
                        title = stringResource(R.string.empty_gallery_title),
                        subtitle = stringResource(R.string.empty_gallery_subtitle)
                    )
                }
                else -> {
                    val columns = uiState.gridDensity.columns
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(1.dp)
                    ) {
                        if (uiState.groupByDate && uiState.dateGroups.isNotEmpty()) {
                            uiState.dateGroups.forEach { group ->
                                item(
                                    key = "header_${group.header}",
                                    span = { GridItemSpan(columns) }
                                ) {
                                    DateHeaderItem(title = group.header)
                                }
                                items(
                                    items = group.items,
                                    key = { item -> item.id }
                                ) { item ->
                                    val isSelected = uiState.selectedIds.contains(item.id)
                                    MediaThumbnail(
                                        item = item,
                                        isSelectionMode = uiState.isSelectionMode,
                                        isSelected = isSelected,
                                        onClick = {
                                            if (uiState.isSelectionMode) {
                                                viewModel.toggleSelection(item.id)
                                            } else {
                                                onNavigateToViewer(item.id)
                                            }
                                        },
                                        onLongClick = {
                                            viewModel.toggleSelection(item.id)
                                        }
                                    )
                                }
                            }
                        } else {
                            items(
                                items = uiState.mediaList,
                                key = { item -> item.id }
                            ) { item ->
                                val isSelected = uiState.selectedIds.contains(item.id)
                                MediaThumbnail(
                                    item = item,
                                    isSelectionMode = uiState.isSelectionMode,
                                    isSelected = isSelected,
                                    onClick = {
                                        if (uiState.isSelectionMode) {
                                            viewModel.toggleSelection(item.id)
                                        } else {
                                            onNavigateToViewer(item.id)
                                        }
                                    },
                                    onLongClick = {
                                        viewModel.toggleSelection(item.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && activity != null) {
        val count = uiState.selectedIds.size
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = if (count > 1) {
                        stringResource(R.string.delete_multiple_title)
                    } else {
                        stringResource(R.string.delete_confirm_title)
                    }
                )
            },
            text = {
                Text(text = stringResource(R.string.delete_confirm_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteSelected(activity) { request ->
                            deleteIntentSenderLauncher.launch(request)
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete_action),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(R.string.cancel_action))
                }
            }
        )
    }
}
