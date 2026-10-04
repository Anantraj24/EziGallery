package com.ezi.gallery.feature.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ezi.gallery.R
import com.ezi.gallery.core.model.MediaItem
import com.ezi.gallery.core.theme.FavoriteHeartColor
import com.ezi.gallery.core.theme.SelectionOverlay
import com.ezi.gallery.core.theme.VideoBadgeBackground
import com.ezi.gallery.core.util.FileUtils

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun MediaThumbnail(
    item: MediaItem,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(1.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        // Thumbnail with Coil: size bounded to avoid full-resolution memory spikes
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(item.contentUri)
                .size(320, 320) // Bounded thumbnail decode size for high scrolling performance on low-RAM
                .crossfade(true)
                .build(),
            contentDescription = item.displayName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Video Badge
        if (item.isVideo) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .background(VideoBadgeBackground, RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Video",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
                if (item.duration > 0L) {
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = FileUtils.formatDuration(item.duration),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Favorite Indicator
        if (item.isFavorite && !isSelectionMode) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Favorite",
                tint = FavoriteHeartColor,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(16.dp)
            )
        }

        // Selection overlay and Checkmark
        if (isSelectionMode) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SelectionOverlay)
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.4f)
                    )
                    .border(
                        1.5.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DateHeaderItem(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp)
    )
}

@Composable
fun EmptyStateView(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.PhotoLibrary,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionButtonText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onActionClick) {
                Text(text = actionButtonText)
            }
        }
    }
}

@Composable
fun LoadingStateView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun PermissionRequestView(
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    isPermanentlyDenied: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.PhotoLibrary,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.permission_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (isPermanentlyDenied) {
                stringResource(R.string.permission_denied_message)
            } else {
                stringResource(R.string.permission_description)
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        if (isPermanentlyDenied) {
            Button(onClick = onOpenSettings) {
                Text(text = stringResource(R.string.open_settings))
            }
        } else {
            Button(onClick = onRequestPermission) {
                Text(text = stringResource(R.string.grant_permission))
            }
        }
    }
}
