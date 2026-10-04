package com.ezi.gallery.feature.viewer

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem as ExoMediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ezi.gallery.EziGalleryApp
import com.ezi.gallery.R
import com.ezi.gallery.core.model.MediaItem
import com.ezi.gallery.core.model.SortOrder
import com.ezi.gallery.core.theme.FavoriteHeartColor
import com.ezi.gallery.core.util.DateFormatter
import com.ezi.gallery.core.util.FileUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaViewerScreen(
    initialMediaId: Long,
    bucketId: String?,
    isFavorites: Boolean,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val app = context.applicationContext as EziGalleryApp
    val repository = app.repository
    val scope = rememberCoroutineScope()

    var mediaList by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
    var showControls by remember { mutableStateOf(true) }
    var showInfoSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(initialMediaId, bucketId, isFavorites) {
        val favoriteIds = repository.favoriteIdsFlow.first()
        val allItems = when {
            isFavorites -> repository.getMediaWithFavorites(SortOrder.DATE_DESC, favoriteIds).filter { it.isFavorite }
            !bucketId.isNullOrBlank() -> repository.getAlbumMediaWithFavorites(bucketId, SortOrder.DATE_DESC, favoriteIds)
            else -> repository.getMediaWithFavorites(SortOrder.DATE_DESC, favoriteIds)
        }
        mediaList = allItems
    }

    if (mediaList.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading...", color = Color.White)
        }
        return
    }

    val initialIndex = mediaList.indexOfFirst { it.id == initialMediaId }.coerceAtLeast(0)
    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { mediaList.size }
    )

    val currentItem = mediaList.getOrNull(pagerState.currentPage) ?: mediaList.first()

    // Delete Launcher for Android 11+
    val deleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onNavigateBack()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Pager of Media
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val item = mediaList[page]
            if (item.isVideo) {
                VideoPlayerView(
                    item = item,
                    onToggleControls = { showControls = !showControls }
                )
            } else {
                ZoomableImageView(
                    item = item,
                    onToggleControls = { showControls = !showControls }
                )
            }
        }

        // Top App Bar Controls
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentItem.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        maxLines = 1
                    )
                    Text(
                        text = DateFormatter.formatGroupHeader(currentItem.dateTaken),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                IconButton(onClick = { showInfoSheet = true }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = Color.White
                    )
                }
            }
        }

        // Bottom Action Bar Controls
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Share
                IconButton(onClick = {
                    val shareIntent = repository.createShareIntent(listOf(currentItem))
                    context.startActivity(android.content.Intent.createChooser(shareIntent, "Share"))
                }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White
                    )
                }

                // Favorite
                IconButton(onClick = {
                    scope.launch {
                        repository.toggleFavorite(currentItem)
                        // update local list state
                        mediaList = mediaList.map {
                            if (it.id == currentItem.id) it.copy(isFavorite = !it.isFavorite) else it
                        }
                    }
                }) {
                    Icon(
                        imageVector = if (currentItem.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (currentItem.isFavorite) FavoriteHeartColor else Color.White
                    )
                }

                // Delete
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White
                    )
                }
            }
        }
    }

    // Info Bottom Sheet
    if (showInfoSheet) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showInfoSheet = false },
            sheetState = sheetState
        ) {
            MediaDetailsSheet(
                item = currentItem,
                onDismiss = { showInfoSheet = false }
            )
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && activity != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = stringResource(R.string.delete_confirm_title)) },
            text = { Text(text = stringResource(R.string.delete_confirm_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        val success = repository.deleteMediaItems(activity, listOf(currentItem)) { request ->
                            deleteLauncher.launch(request)
                        }
                        if (success) {
                            onNavigateBack()
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.delete_action), color = MaterialTheme.colorScheme.error)
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

@Composable
fun ZoomableImageView(
    item: MediaItem,
    onToggleControls: () -> Unit
) {
    val context = LocalContext.current
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 4f)
        if (scale > 1f) {
            offset += offsetChange
        } else {
            offset = Offset.Zero
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onToggleControls() },
                    onDoubleTap = {
                        if (scale > 1f) {
                            scale = 1f
                            offset = Offset.Zero
                        } else {
                            scale = 2.5f
                        }
                    }
                )
            }
            .transformable(state = transformState),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(item.contentUri)
                .crossfade(true)
                .build(),
            contentDescription = item.displayName,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
        )
    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerView(
    item: MediaItem,
    onToggleControls: () -> Unit
) {
    val context = LocalContext.current
    val exoPlayer = remember(item.contentUri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(ExoMediaItem.fromUri(item.contentUri))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onToggleControls
            ),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun MediaDetailsSheet(
    item: MediaItem,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .navigationBarsPadding()
    ) {
        Text(
            text = "Details",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        DetailRow(label = "Filename", value = item.displayName)
        DetailRow(label = "Date Taken", value = DateFormatter.formatDetailedDateTime(item.dateTaken))
        DetailRow(label = "File Size", value = FileUtils.formatFileSize(item.size))
        if (item.width > 0 && item.height > 0) {
            DetailRow(label = "Dimensions", value = "${item.width} × ${item.height} pixels")
        }
        if (item.isVideo && item.duration > 0) {
            DetailRow(label = "Duration", value = FileUtils.formatDuration(item.duration))
        }
        if (item.bucketName.isNotBlank()) {
            DetailRow(label = "Album", value = item.bucketName)
        }
        DetailRow(label = "Type", value = item.mimeType)

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
