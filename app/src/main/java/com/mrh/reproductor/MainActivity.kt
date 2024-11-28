package com.mrh.reproductor

import android.annotation.SuppressLint
import android.graphics.Bitmap
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.mrh.reproductor.ui.theme.ReproductorTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val viewModel = AlbumsViewModel(this@MainActivity)
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

    @androidx.annotation.OptIn(UnstableApi::class)
    @Composable
    fun PlaybackSlider(exoPlayer: ExoPlayerViewModel) {
        var playbackPosition by remember { mutableStateOf(0L) }
        var duration by remember { mutableLongStateOf(0L) }
        var isUserInteracting by remember { mutableStateOf(false) }
        exoPlayer.setListener(object : ExoPlayerViewModel.ExoPlayerListener {
            override fun onTrackPlaying(trackUrl: String) {
                duration = exoPlayer.player?.duration!!
                playbackPosition = exoPlayer.player?.currentPosition!!
            }
        })

        LaunchedEffect(exoPlayer) {
            exoPlayer.player?.addListener(object : Player.Listener {
                @Deprecated("Deprecated in Java")
                override fun onPositionDiscontinuity(reason: Int) {
                    playbackPosition = exoPlayer.player?.currentPosition!!
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        duration = verifyNullDuration(exoPlayer.player?.duration).toLong()
                    }
                }
            })
        }

        LaunchedEffect(key1 = exoPlayer.player?.isPlaying) {
            while (exoPlayer.player!!.isPlaying) {
                playbackPosition = exoPlayer.player!!.currentPosition
                delay(100) // Update every 100 milliseconds
            }
        }

        if(!exoPlayer.player?.duration!!.equals(null)){
            Slider(
                value = playbackPosition.toFloat(),
                onValueChange = { newPosition ->
                    playbackPosition = newPosition.coerceAtLeast(0f).toLong()
                    exoPlayer.player?.seekTo(playbackPosition)
                },
                valueRange = 0f..verifyNullDuration(exoPlayer.player?.duration),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )
        }
    }

    fun verifyNullDuration(duration: Long?): Float {
        if (duration != null) {
            return duration.toFloat()
        }
        return 1f
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("ResourceType")
    @Composable
    fun MusicNavBar(navController: NavHostController, player: ExoPlayerViewModel) {
        var isPlaying by remember { mutableStateOf(false) }
        var titulo by remember { mutableStateOf("") }
        var artist by remember { mutableStateOf("") }
        var caratula by remember { mutableStateOf<Bitmap?>(null) }
        var modalVisible by remember { mutableStateOf(false) }
        player.setListener(object : ExoPlayerViewModel.ExoPlayerListener {
            override fun onTrackPlaying(trackUrl: String) {
                titulo = player.getSongTitle()
                artist = player.getArtists()
                isPlaying = true
                caratula = player.getCover()
            }
        })
        Card(
            onClick = {
                modalVisible = !modalVisible
            }
        ) {
            if (modalVisible) {
                ModalBottomSheet(
                    onDismissRequest = {
                        modalVisible = false
                    },
                    sheetMaxWidth = Dp.Unspecified,
                    sheetState = rememberModalBottomSheetState(
                        skipPartiallyExpanded = true
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        caratula?.asImageBitmap()?.let {
                            Image(
                                bitmap = it,
                                contentDescription = null,
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(10.dp)
                                    )
                                    .size(280.dp)
                            )
                        }
                        Column (
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Text(titulo, fontWeight = FontWeight.Bold, fontSize = 25.sp)
                            Text(artist)
                        }
                        PlaybackSlider(exoPlayer = player)
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
                }
            }
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
                    caratula?.asImageBitmap()?.let {
                        Image(
                            bitmap = it,
                            contentDescription = null,
                            modifier = Modifier.clip(
                                RoundedCornerShape(10.dp)
                            )
                        )
                    }
                    Column {
                        Text(titulo, fontWeight = FontWeight.Bold)
                        Text(artist)
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
                        navController = navController,
                        player = player
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

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (album.cover != 0) {
                    Image(
                        painter = painterResource(album.cover),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(360.dp)
                            .blur(50.dp),
                        contentScale = ContentScale.FillWidth
                    )
                } else {
                    player.getAlbumArt(this@MainActivity, album.canciones[0].archivo)
                        ?.let { it1 ->
                            Image(
                                bitmap = it1.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(360.dp)
                                    .blur(50.dp),
                            )
                        }
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (album.cover != 0) {
                        Image(
                            painter = painterResource(album.cover),
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(shape = RoundedCornerShape(13.dp))
                                .border(1.dp, Color.Gray, RoundedCornerShape(13.dp))
                        )
                    } else {
                        player.getAlbumArt(this@MainActivity, album.canciones[0].archivo)
                            ?.let { it1 ->
                                Image(
                                    bitmap = it1.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clip(shape = RoundedCornerShape(13.dp))
                                        .border(1.dp, Color.Gray, RoundedCornerShape(13.dp))
                                )
                            }
                    }
                    Spacer(modifier = Modifier.padding(6.dp))
                    Text(
                        album.nombre,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 25.sp
                    )
                    Spacer(modifier = Modifier.padding(2.dp))
                    Text(album.artista, fontSize = 20.sp, color = Color.White)
                    IconButton(
                        onClick = {
                            player.returnPlaying()
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PlayArrow,
                            contentDescription = null,
                        )
                    }
                }

            }
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                album.canciones.forEach { song ->
                    LaunchedEffect(player) {
                        player.addToPlaylist(song.archivo, this@MainActivity)
                        player.player?.prepare()
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .height(90.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        ),
                        onClick = {
                            player.playFromPlaylist(album.canciones.indexOf(song))
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (album.cover != 0) {
                                Image(
                                    painter = painterResource(album.cover),
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            } else {
                                player.getAlbumArt(this@MainActivity, song.archivo)
                                    ?.let { it1 ->
                                        Image(
                                            bitmap = it1.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                    }
                            }
                            Column {
                                Text(song.nombre, fontWeight = FontWeight.ExtraBold)
                                Text(song.artista)
                            }
                        }
                    }
                }
            }

        }

    }

    @SuppressLint("ResourceType")
    @Composable
    fun AlbumsView(
        viewModel: AlbumsViewModel,
        navController: NavHostController,
        player: ExoPlayerViewModel
    ) {
        val generos: List<String> =
            Generos.entries.toTypedArray().map { genero -> genero.nombre }
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
                        pos = albums.indexOf(album),
                        player = player
                    )
                }
            }

        }

    }

    @SuppressLint("ResourceType")
    @Composable
    fun AlbumCard(
        album: Album,
        navController: NavHostController,
        pos: Int,
        player: ExoPlayerViewModel
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            onClick = {
                navController.navigate("album_view/$pos")
            },
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                if (album.cover != 0) {
                    Image(
                        painter = painterResource(album.cover),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(13.dp))
                    )
                } else {
                    player.getAlbumArt(this@MainActivity, album.canciones[0].archivo)
                        ?.let { it1 ->
                            Image(
                                bitmap = it1.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(13.dp))
                            )
                        }
                }
                Text(
                    album.nombre,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(album.artista)
            }


        }


    }
}



