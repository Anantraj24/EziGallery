package com.ezi.gallery.feature.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ezi.gallery.R
import com.ezi.gallery.feature.common.EmptyStateView
import com.ezi.gallery.feature.common.LoadingStateView
import com.ezi.gallery.feature.common.MediaThumbnail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateToViewer: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.nav_search),
                        style = MaterialTheme.typography.headlineMedium
                    )
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
        ) {
            // Search Input Field
            OutlinedTextField(
                value = uiState.query,
                onValueChange = { viewModel.onQueryChange(it) },
                placeholder = { Text("Search photos & videos by name...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isSearching -> {
                        LoadingStateView()
                    }
                    uiState.hasSearched && uiState.results.isEmpty() -> {
                        EmptyStateView(
                            title = stringResource(R.string.empty_search_title),
                            subtitle = stringResource(R.string.empty_search_subtitle)
                        )
                    }
                    !uiState.hasSearched -> {
                        EmptyStateView(
                            title = "Search Media",
                            subtitle = "Type a filename, extension, or folder name to find items."
                        )
                    }
                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(1.dp)
                        ) {
                            items(
                                items = uiState.results,
                                key = { it.id }
                            ) { item ->
                                MediaThumbnail(
                                    item = item,
                                    isSelectionMode = false,
                                    isSelected = false,
                                    onClick = { onNavigateToViewer(item.id) },
                                    onLongClick = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
