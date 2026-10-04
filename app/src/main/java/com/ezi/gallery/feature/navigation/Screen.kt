package com.ezi.gallery.feature.navigation

sealed class Screen(val route: String) {
    data object Photos : Screen("photos")
    data object Albums : Screen("albums")
    data object Search : Screen("search")
    data object More : Screen("more")
    data object Favorites : Screen("favorites")
    data object Settings : Screen("settings")

    data object AlbumDetail : Screen("album_detail/{bucketId}/{bucketName}") {
        fun createRoute(bucketId: String, bucketName: String) = "album_detail/$bucketId/$bucketName"
    }

    data object Viewer : Screen("viewer/{mediaId}?bucketId={bucketId}&isFavorites={isFavorites}") {
        fun createRoute(mediaId: Long, bucketId: String? = null, isFavorites: Boolean = false): String {
            val bId = bucketId ?: ""
            return "viewer/$mediaId?bucketId=$bId&isFavorites=$isFavorites"
        }
    }
}
