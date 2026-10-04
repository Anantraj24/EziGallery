package com.ezi.gallery.feature.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ezi.gallery.R
import com.ezi.gallery.feature.albums.AlbumDetailScreen
import com.ezi.gallery.feature.albums.AlbumsScreen
import com.ezi.gallery.feature.albums.AlbumsViewModel
import com.ezi.gallery.feature.favorites.FavoritesScreen
import com.ezi.gallery.feature.favorites.FavoritesViewModel
import com.ezi.gallery.feature.more.MoreScreen
import com.ezi.gallery.feature.photos.PhotosScreen
import com.ezi.gallery.feature.photos.PhotosViewModel
import com.ezi.gallery.feature.search.SearchScreen
import com.ezi.gallery.feature.search.SearchViewModel
import com.ezi.gallery.feature.settings.SettingsScreen
import com.ezi.gallery.feature.settings.SettingsViewModel
import com.ezi.gallery.feature.viewer.MediaViewerScreen

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide bottom navigation in viewer and detail sub-screens
    val isBottomBarVisible = currentRoute in listOf(
        Screen.Photos.route,
        Screen.Albums.route,
        Screen.Search.route,
        Screen.More.route
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (isBottomBarVisible) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Screen.Photos.route,
                        onClick = {
                            navController.navigate(Screen.Photos.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null) },
                        label = { Text(text = stringResource(R.string.nav_photos)) }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.Albums.route,
                        onClick = {
                            navController.navigate(Screen.Albums.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = Icons.Default.Folder, contentDescription = null) },
                        label = { Text(text = stringResource(R.string.nav_albums)) }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.Search.route,
                        onClick = {
                            navController.navigate(Screen.Search.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                        label = { Text(text = stringResource(R.string.nav_search)) }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.More.route,
                        onClick = {
                            navController.navigate(Screen.More.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = Icons.Default.MoreHoriz, contentDescription = null) },
                        label = { Text(text = stringResource(R.string.nav_more)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Photos.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Photos Screen
            composable(Screen.Photos.route) {
                val photosViewModel: PhotosViewModel = viewModel(factory = PhotosViewModel.provideFactory(context))
                PhotosScreen(
                    viewModel = photosViewModel,
                    onNavigateToViewer = { mediaId ->
                        navController.navigate(Screen.Viewer.createRoute(mediaId = mediaId))
                    },
                    onNavigateToSearch = {
                        navController.navigate(Screen.Search.route)
                    }
                )
            }

            // Albums Screen
            composable(Screen.Albums.route) {
                val albumsViewModel: AlbumsViewModel = viewModel(factory = AlbumsViewModel.provideFactory(context))
                AlbumsScreen(
                    viewModel = albumsViewModel,
                    onAlbumClick = { bucketId, bucketName ->
                        navController.navigate(Screen.AlbumDetail.createRoute(bucketId, bucketName))
                    }
                )
            }

            // Album Detail Screen
            composable(
                route = Screen.AlbumDetail.route,
                arguments = listOf(
                    navArgument("bucketId") { type = NavType.StringType },
                    navArgument("bucketName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val bucketId = backStackEntry.arguments?.getString("bucketId") ?: ""
                val bucketName = backStackEntry.arguments?.getString("bucketName") ?: ""
                val albumsViewModel: AlbumsViewModel = viewModel(factory = AlbumsViewModel.provideFactory(context))
                AlbumDetailScreen(
                    bucketId = bucketId,
                    bucketName = bucketName,
                    viewModel = albumsViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToViewer = { mediaId ->
                        navController.navigate(Screen.Viewer.createRoute(mediaId = mediaId, bucketId = bucketId))
                    }
                )
            }

            // Search Screen
            composable(Screen.Search.route) {
                val searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.provideFactory(context))
                SearchScreen(
                    viewModel = searchViewModel,
                    onNavigateToViewer = { mediaId ->
                        navController.navigate(Screen.Viewer.createRoute(mediaId = mediaId))
                    }
                )
            }

            // More Screen
            composable(Screen.More.route) {
                MoreScreen(
                    onNavigateToFavorites = {
                        navController.navigate(Screen.Favorites.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }

            // Favorites Screen
            composable(Screen.Favorites.route) {
                val favoritesViewModel: FavoritesViewModel = viewModel(factory = FavoritesViewModel.provideFactory(context))
                FavoritesScreen(
                    viewModel = favoritesViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToViewer = { mediaId ->
                        navController.navigate(Screen.Viewer.createRoute(mediaId = mediaId, isFavorites = true))
                    }
                )
            }

            // Settings Screen
            composable(Screen.Settings.route) {
                val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.provideFactory(context))
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Media Viewer Screen
            composable(
                route = Screen.Viewer.route,
                arguments = listOf(
                    navArgument("mediaId") { type = NavType.LongType },
                    navArgument("bucketId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("isFavorites") {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) { backStackEntry ->
                val mediaId = backStackEntry.arguments?.getLong("mediaId") ?: 0L
                val bucketId = backStackEntry.arguments?.getString("bucketId")
                val isFavorites = backStackEntry.arguments?.getBoolean("isFavorites") ?: false
                MediaViewerScreen(
                    initialMediaId = mediaId,
                    bucketId = bucketId,
                    isFavorites = isFavorites,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
