package com.mrh.reproductor

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.mrh.reproductor.ui.theme.ReproductorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val viewModel = AlbumsViewModel()
            viewModel.albums
            ReproductorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationHost(
                        navController = navController,
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    @Composable
    fun NavigationHost(
        navController: NavHostController,
        viewModel: AlbumsViewModel,
        modifier: Modifier = Modifier)
    {
        NavHost(
            startDestination = "home",
            navController = navController,
            modifier = modifier.fillMaxSize()
        ) {
            navigation(
                startDestination = "albums_view",
                route = "home"
            ){
                composable(route = "albums_view") {
                    AlbumsView(
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    @SuppressLint("ResourceType")
    @Composable
    fun AlbumsView(viewModel: AlbumsViewModel) {
        val generos: List<String> = Generos.entries.toTypedArray().map { genero -> genero.nombre }
        val albums = viewModel.getAlbums()
        var selectedFilter by remember { mutableStateOf(setOf<String>()) }

        Column(modifier = Modifier.padding(16.dp)){
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())){
                generos.forEach { genero ->
                    FilterChip(
                        label = {
                            Text(genero)
                        },
                        onClick = {
                            selectedFilter = if(genero in selectedFilter){
                                selectedFilter - genero
                            }else{
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
                items(albums.filter { album -> if(selectedFilter.isEmpty()) true else album.genero in selectedFilter }){ album ->
                    AlbumCard(album = album)
                }
            }
        }

    }

    @Composable
    fun AlbumCard(album: Album){
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            onClick = {

            }
        ){
            Column (modifier= Modifier.fillMaxWidth(),verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start){
                Image(painter = painterResource(album.cover), contentDescription = null, modifier = Modifier.fillMaxSize())
                Text(album.nombre)
            }

        }
    }

}



