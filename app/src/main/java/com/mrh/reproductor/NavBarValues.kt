package com.mrh.reproductor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavBarValues (
    val label: String? = null,
    val icon: ImageVector? = null,
    val route: String,
    val root: String
) {
    INICIO(
        label = "Inicio",
        icon = Icons.Filled.Home,
        route = "albums_view",
        root = "home"
    ),
    ALBUM_VIEW(
        route = "album_view/{pos}",
        root = "home"
    ),
    PLAYLISTS(
    label = "Playlists",
    icon = Icons.Filled.Menu,
    route = "playlists",
    root = "others"
    )
}