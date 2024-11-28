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
    var albums: LiveData<List<Album>> = _albums

    var context = context


    private fun crearCancionDesdeArchivo(archivo: Int): Song {
        val datos = ExoPlayerViewModel().getMP3Metadata(resId = archivo, context = context)
        return Song(
            nombre = datos["Title"]!!,
            archivo = archivo,
            artista = datos["Artist"]!!
        )
    }


    private fun cargarDatos() {
        val listAlbums: ArrayList<Album> = ArrayList()
        listAlbums.add(
            Album(
                artista = "Quevedo",
                nombre = "Buenas Noches",
                cover = 0,
                genero = Generos.LATINO.nombre,
                canciones = listOf(
                    crearCancionDesdeArchivo(R.raw.kassandra),
                    crearCancionDesdeArchivo(R.raw.duro),
                    crearCancionDesdeArchivo(R.raw.iguales),
                    crearCancionDesdeArchivo(R.raw.granvia),
                    crearCancionDesdeArchivo(R.raw.chapiadora),
                    crearCancionDesdeArchivo(R.raw.poratras),
                    crearCancionDesdeArchivo(R.raw.catorcefebreros),
                    crearCancionDesdeArchivo(R.raw.lacientoventicinco),
                    crearCancionDesdeArchivo(R.raw.halo),
                    crearCancionDesdeArchivo(R.raw.mrmoondial),
                    crearCancionDesdeArchivo(R.raw.queascodetodo),
                    crearCancionDesdeArchivo(R.raw.noemu),
                    crearCancionDesdeArchivo(R.raw.shibatto),
                    crearCancionDesdeArchivo(R.raw.losdiascontados),
                    crearCancionDesdeArchivo(R.raw.elestribillo),
                    crearCancionDesdeArchivo(R.raw.amanecio),
                    crearCancionDesdeArchivo(R.raw.tefalle),
                    crearCancionDesdeArchivo(R.raw.buenasnoches),
                )
            )
        )

        this.albums = MutableLiveData(listAlbums)
    }

    fun getAlbums(): ArrayList<Album> {
        if (albums.value === null) {
            cargarDatos()
        }

        return albums.value as ArrayList<Album>
    }
}