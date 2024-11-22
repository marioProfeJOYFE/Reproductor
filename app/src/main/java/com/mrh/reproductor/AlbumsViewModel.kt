package com.mrh.reproductor

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AlbumsViewModel(context: Context) : ViewModel() {
    // Crea una lista de albumes VACIA
    private val _albums = MutableLiveData<List<Album>>()
    // VARIABLE que contiene el dato SIEMPRE ACTUALIZADO
    var albums : LiveData<List<Album>> = _albums

    var context = context



    private fun crearCancionDesdeArchivo(archivo: Int): Song {
        val datos = ExoPlayerViewModel().getMP3Metadata(resId=archivo, context = context)
        return Song(
            nombre = datos["Title"]!!,
            archivo = archivo,
            artista = datos["Artist"]!!
        )
    }


    private fun cargarDatos(){
        val listAlbums: ArrayList<Album> = ArrayList()
        listAlbums.add(
            Album(
                artista = "Mario",
                nombre = "Album",
                cover = R.raw.cover,
                genero = Generos.POP.nombre,
                canciones = listOf(
                    crearCancionDesdeArchivo(R.raw.antes),
                    crearCancionDesdeArchivo(R.raw.tengofe)
                )
            )
        )
        listAlbums.add(
            Album(
                artista = "Mario",
                nombre = "Album",
                cover = R.raw.cover,
                genero = Generos.ROCK.nombre,
                canciones = listOf(
                    Song(
                        nombre = "Cancion",
                        archivo = R.raw.antes,
                        artista = "Mario"
                    ),
                    Song(
                        nombre = "Cancion",
                        archivo = R.raw.antes,
                        artista = "Mario"
                    )
                    ,
                    Song(
                        nombre = "Cancion",
                        archivo = R.raw.antes,
                        artista = "Mario"
                    ),
                    Song(
                        nombre = "Cancion",
                        archivo = R.raw.antes,
                        artista = "Mario"
                    ),
                    Song(
                        nombre = "Cancion",
                        archivo = R.raw.antes,
                        artista = "Mario"
                    ),
                    Song(
                        nombre = "Cancion",
                        archivo = R.raw.antes,
                        artista = "Mario"
                    ),
                    Song(
                        nombre = "Cancion",
                        archivo = R.raw.antes,
                        artista = "Mario"
                    ),
                    Song(
                        nombre = "Cancion",
                        archivo = R.raw.antes,
                        artista = "Mario"
                    ),
                    Song(
                        nombre = "Cancion",
                        archivo = R.raw.antes,
                        artista = "Mario"
                    )
                )
            )
        )
        listAlbums.add(
            Album(
                artista = "Mario",
                nombre = "Album",
                cover = R.raw.cover,
                genero = Generos.HARD.nombre,
                canciones = listOf(
                    Song(
                        nombre = "Cancion",
                        archivo = R.raw.antes,
                        artista = "Mario"
                    ),
                    Song(
                        nombre = "Cancion",
                        archivo = R.raw.antes,
                        artista = "Mario"
                    )
                )
            )
        )

        this.albums = MutableLiveData(listAlbums)
    }

    fun getAlbums() : ArrayList<Album> {
        if (albums.value === null) {
            cargarDatos()
        }

        return albums.value as ArrayList<Album>
    }
}