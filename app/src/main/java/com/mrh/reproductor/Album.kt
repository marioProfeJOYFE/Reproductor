package com.mrh.reproductor

data class Album (
    val nombre: String,
    val artista: String,
    val cover: Int,
    val genero: String,
    val canciones: List<Song>
)
