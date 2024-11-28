package com.mrh.reproductor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.annotation.OptIn
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class ExoPlayerViewModel : ViewModel() {

    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _currentTrack = MutableLiveData<String?>(null)
    val currentTrack: LiveData<String?> = _currentTrack

    var player: ExoPlayer? = null
    private var listener: ExoPlayerListener? = null

    fun initializePlayer(context: Context) {
        player = ExoPlayer.Builder(context).build()
        // ... other player configurations ...
    }

    fun playAlbum(album: Album, context: Context) {
        player?.release()
        viewModelScope.launch {
            album.canciones.forEach { song ->
                addToPlaylist(song.archivo, context)
            }
            player?.prepare()
            player?.play()
            _isPlaying.value = true
            _currentTrack.value = "android.resource://${context.packageName}/${album.canciones[0].archivo}"
            listener?.onTrackPlaying("android.resource://${context.packageName}/${album.canciones[0].archivo}")
        }
    }

    fun playFromPlaylist(index: Int) {
        player?.seekTo(index, 0)
        player?.play()
        _isPlaying.value = true
        listener?.onTrackPlaying(player?.currentMediaItem?.mediaId.toString())
    }

    fun addToPlaylist(archivo: Int, context: Context) {
        val trackUrl = "android.resource://${context.packageName}/${archivo}"
        val data = getMP3Metadata(context, archivo)
        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(data["Title"])
            .setArtist(data["Artist"])
            .setArtworkData(getAlbumArt(context, archivo)?.toByteArray(), MediaMetadata.PICTURE_TYPE_FRONT_COVER)
            .build()
        val mediaItem = MediaItem.Builder()
            .setMediaMetadata(mediaMetadata).setUri(trackUrl).build()
        player?.addMediaItem(mediaItem)
    }

    @OptIn(UnstableApi::class)
    fun playTrack(archivo: Int, context: Context) {
        val trackUrl = "android.resource://${context.packageName}/${archivo}"
        val data = getMP3Metadata(context, archivo)
        Log.d("ExoPlayerViewModel", "Title: ${data["Title"]}")
        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(data["Title"])
            .setArtist(data["Artist"])
            .setArtworkData(getAlbumArt(context, archivo)?.toByteArray(), MediaMetadata.PICTURE_TYPE_FRONT_COVER)
            .build()
        val mediaItem = MediaItem.Builder()
            .setMediaMetadata(mediaMetadata).setUri(trackUrl).build()
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
        _isPlaying.value = true
        _currentTrack.value = trackUrl
        listener?.onTrackPlaying(trackUrl) // Notify the listener
    }


    fun Bitmap.toByteArray(): ByteArray {
        val stream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    fun getMP3Metadata(context: Context, resId: Int): Map<String, String> {
        val retriever = MediaMetadataRetriever()
        val uri =
            "android.resource://${context.packageName}/$resId"
        retriever.setDataSource(context, android.net.Uri.parse(uri))
        val metadata = mapOf(
            "Title" to (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                ?: "Unknown"),
            "Artist" to (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                ?: "Unknown"),
            "Album" to (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                ?: "Unknown"),
            "Duration" to (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?: "Unknown")
        )
        retriever.release()
        return metadata
    }

    fun getAlbumArt(context: Context, resId: Int): Bitmap? {
        val retriever = MediaMetadataRetriever()
        val uri =
            "android.resource://${context.packageName}/$resId"
        retriever.setDataSource (context, android.net.Uri.parse(uri))
        val art = retriever.embeddedPicture
        retriever.release()
        return if (art != null) {
            BitmapFactory.decodeByteArray(
                art,
                0,
                art.size
            )
        } else {
            null
        }
    }


        fun pausePlayer() {
            player?.pause()
            _isPlaying.value = false
        }

        fun returnPlaying() {
            player?.play()
            _isPlaying.value = true
        }

        fun seekToNext() {
            player?.seekToNext()
            listener?.onTrackPlaying(player?.currentMediaItem?.mediaId.toString())
        }

        fun seekToPrevious() {
            player?.seekToPrevious()
            listener?.onTrackPlaying(player?.currentMediaItem?.mediaId.toString())
        }

        fun setListener(listener: ExoPlayerListener) {
            this.listener = listener
        }

        fun getCurrentTrack(): String? {
            return currentTrack.value
        }

        fun getSongTitle(): String {
            return player?.mediaMetadata?.title.toString()

        }

        override fun onCleared() {
            super.onCleared()
            player?.release()
            player = null
        }

        fun getArtists(): String {
            return player?.mediaMetadata?.artist.toString()
        }

        fun getCover(): Bitmap? {
            return player?.mediaMetadata?.artworkData?.let {
                BitmapFactory.decodeByteArray(it, 0, it.size)
            }
        }

        interface ExoPlayerListener {
            fun onTrackPlaying(trackUrl: String)
        }
    }