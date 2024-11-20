package com.mrh.reproductor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AlbumsViewModel : ViewModel() {
    // Crea una lista de albumes VACIA
    private val _albums = MutableLiveData<List<Album>>()
    // VARIABLE que contiene el dato SIEMPRE ACTUALIZADO
    var albums : LiveData<List<Album>> = _albums

    private fun cargarDatos(){
        val listAlbums: ArrayList<Album> = ArrayList()
        listAlbums.add(
            Album(
                artista = "Mario",
                nombre = "Album",
                cover = R.raw.cover,
                genero = Generos.POP.nombre,
                canciones = listOf(
                    Song(
                        nombre = "Rojo",
                        archivo = R.raw.rojo,
                        artista = "J Balvin"
                    ),
                    Song(
                        nombre = "8 dias",
                        archivo = R.raw.ochodias,
                        artista = "Feid"
                    )
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
                        archivo = R.raw.rojo,
                        artista = "Mario"
                    ),
                    Song(
                        nombre = "Cancion",
                        archivo = R.raw.rojo,
                        artista = "Mario"
                    )
                    ,
                    Song(
                        nombre = "Cancion",
                        archivo = R.raw.rojo,
                        artista = "Mario"
                    ),
                    Song(
                        nombre = "Cancion",
                        archivo = R.raw.rojo,
                        artista = "Mario"
                    ),
                    Song(
                        nombre = "Cancion",
                        archivo = R.raw.rojo,
                        artista = "Mario"
                    ),
                    Song(
                        nombre = "Cancion",
                        archivo = R.raw.rojo,
                        artista = "Mario"
                    ),
                    Song(
                        nombre = "Cancion",
                        archivo = R.raw.rojo,
                        artista = "Mario"
                    ),
                    Song(
                        nombre = "Cancion",
                        archivo = R.raw.rojo,
                        artista = "Mario"
                    ),
                    Song(
                        nombre = "Cancion",
                        archivo = R.raw.rojo,
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
                        archivo = R.raw.rojo,
                        artista = "Mario"
                    ),
                    Song(
                        nombre = "Cancion",
                        archivo = R.raw.rojo,
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