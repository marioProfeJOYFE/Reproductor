package com.mrh.reproductor

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.FastForward
import androidx.compose.material.icons.outlined.FastRewind
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.mrh.reproductor.ui.theme.ReproductorTheme
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val viewModel = AlbumsViewModel()
            val player = ExoPlayerViewModel()
            player.initializePlayer(this@MainActivity)
            viewModel.albums
            ReproductorTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        MusicNavBar(navController, player)
                    }
                ) { innerPadding ->
                    NavigationHost(
                        navController = navController,
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding),
                        player = player
                    )
                }
            }
        }
    }

    @SuppressLint("ResourceType")
    @Composable
    fun MusicNavBar(navController: NavHostController, player: ExoPlayerViewModel) {
        var isPlaying by remember { mutableStateOf(false) }
        var titulo by remember { mutableStateOf("") }
        var artist by remember { mutableStateOf("") }
        player.setListener(object : ExoPlayerViewModel.ExoPlayerListener {
            override fun onTrackPlaying(trackUrl: String) {
                titulo = player.getSongTitle()
                artist = player.getArtists()
                isPlaying = true
            }
        })
        Card {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 9.dp)
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = painterResource(R.raw.cover),
                        contentDescription = null,
                        modifier = Modifier.clip(
                            RoundedCornerShape(10.dp)
                        )
                    )
                    Column {
                        Text(titulo, fontWeight = FontWeight.Bold)
                        Text("Artista")
                    }
                }
                Row {
                    IconButton(
                        onClick = {
                            player.seekToPrevious()
                        }
                    ) {
                        Icon(imageVector = Icons.Outlined.FastRewind, contentDescription = null)
                    }
                    IconButton(
                        onClick = {
                            if (isPlaying) {
                                player.pausePlayer()
                            } else {
                                player.returnPlaying()
                            }
                            isPlaying = !isPlaying
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        if (isPlaying) {
                            Icon(imageVector = Icons.Outlined.Pause, contentDescription = null)
                        } else {
                            Icon(imageVector = Icons.Outlined.PlayArrow, contentDescription = null)
                        }
                    }
                    IconButton(
                        onClick = {
                            player.seekToNext()
                        }
                    ) {
                        Icon(imageVector = Icons.Outlined.FastForward, contentDescription = null)
                    }
                }
            }
            NavigationBar(
                containerColor = Color.Transparent
            ) {
                val lista = listOf(NavBarValues.INICIO, NavBarValues.PLAYLISTS)
                lista.forEach { element ->
                    NavigationBarItem(
                        icon = {
                            Icon(element.icon!!, contentDescription = null)
                        },
                        onClick = {
                            navController.navigate(element.root)
                        },
                        selected = true
                    )

                }
            }
        }

    }

    @Composable
    fun NavigationHost(
        navController: NavHostController,
        viewModel: AlbumsViewModel,
        modifier: Modifier = Modifier,
        player: ExoPlayerViewModel
    ) {
        NavHost(
            startDestination = NavBarValues.INICIO.root,
            navController = navController,
            modifier = modifier.fillMaxSize()
        ) {
            navigation(
                startDestination = NavBarValues.INICIO.route,
                route = NavBarValues.INICIO.root
            ) {
                composable(route = NavBarValues.INICIO.route) {
                    AlbumsView(
                        viewModel = viewModel,
                        navController = navController
                    )
                }
                composable(route = NavBarValues.ALBUM_VIEW.route) { direccion ->
                    val pos = direccion.arguments?.getString("pos").toString().toInt()
                    AlbumView(
                        album = viewModel.getAlbums()[pos],
                        navController = navController,
                        player = player
                    )
                }
            }
            composable(route = NavBarValues.PLAYLISTS.root) {

            }
        }
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AlbumView(album: Album, navController: NavHostController, player: ExoPlayerViewModel) {
        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Text(album.nombre)
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    Image(
                        painter = painterResource(album.cover),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(360.dp)
                            .blur(50.dp),
                        contentScale = ContentScale.FillWidth
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(album.cover),
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(shape = RoundedCornerShape(13.dp))
                                .border(1.dp, Color.Gray, RoundedCornerShape(13.dp))
                        )
                        Spacer(modifier = Modifier.padding(6.dp))
                        Text(
                            album.nombre,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 25.sp
                        )
                        Spacer(modifier = Modifier.padding(2.dp))
                        Text(album.artista, fontSize = 20.sp, color = Color.White)

                    }

                }
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    album.canciones.forEach { song ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .height(90.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent
                            ),
                            onClick = {
                                player.playTrack(song.archivo, context = this@MainActivity)
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(album.cover),
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Column {
                                    Text("Hola", fontWeight = FontWeight.ExtraBold)
                                    Text(song.artista)
                                }
                            }
                        }
                    }
                }

            }
        }

    }

    @SuppressLint("ResourceType")
    @Composable
    fun AlbumsView(viewModel: AlbumsViewModel, navController: NavHostController) {
        val generos: List<String> = Generos.entries.toTypedArray().map { genero -> genero.nombre }
        val albums = viewModel.getAlbums()
        var selectedFilter by remember { mutableStateOf(setOf<String>()) }

        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                generos.forEach { genero ->
                    FilterChip(
                        label = {
                            Text(genero)
                        },
                        onClick = {
                            selectedFilter = if (genero in selectedFilter) {
                                selectedFilter - genero
                            } else {
                                selectedFilter + genero
                            }
                        },
                        selected = genero in selectedFilter,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }

            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(17.dp),
                verticalArrangement = Arrangement.Top,

                ) {
                items(albums.filter { album -> if (selectedFilter.isEmpty()) true else album.genero in selectedFilter }) { album ->
                    AlbumCard(
                        album = album,
                        navController = navController,
                        pos = albums.indexOf(album)
                    )
                }
            }

        }

    }

    @SuppressLint("ResourceType")
    @Composable
    fun AlbumCard(album: Album, navController: NavHostController, pos: Int) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            onClick = {
                navController.navigate("album_view/$pos")
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Image(
                    painter = painterResource(R.raw.cover),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
                Text(album.nombre)
            }

        }
    }


}



