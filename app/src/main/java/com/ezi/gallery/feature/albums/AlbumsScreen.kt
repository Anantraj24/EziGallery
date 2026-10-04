package com.ezi.gallery.feature.albums

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ezi.gallery.R
import com.ezi.gallery.core.model.AlbumItem
import com.ezi.gallery.feature.common.EmptyStateView
import com.ezi.gallery.feature.common.LoadingStateView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumsScreen(
    viewModel: AlbumsViewModel,
    onAlbumClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAlbums()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.nav_albums),
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingStateView()
                }
                uiState.albums.isEmpty() -> {
                    EmptyStateView(
                        title = "No Albums Found",
                        subtitle = "Photos and videos on your device are grouped by folder."
                    )
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        items(
                            items = uiState.albums,
                            key = { it.bucketId }
                        ) { album ->
                            AlbumCard(
                                album = album,
                                onClick = { onAlbumClick(album.bucketId, album.bucketName) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlbumCard(
    album: AlbumItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .padding(6.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (album.coverUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(album.coverUri)
                        .size(360, 360)
                        .crossfade(true)
                        .build(),
                    contentDescription = album.bucketName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxSize(0.4f)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = album.bucketName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = "${album.itemCount} items",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
