package com.mrh.reproductor

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer

class ExoPlayerViewModel : ViewModel() {

    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _currentTrack = MutableLiveData<String?>(null)
    val currentTrack: LiveData<String?> = _currentTrack

    private var player: ExoPlayer? = null
    private var listener: ExoPlayerListener? = null

    fun initializePlayer(context: Context) {
        player = ExoPlayer.Builder(context).build()
        // ... other player configurations ...
    }

    fun playTrack(trackUrl: String) {
        val mediaMetadata = MediaMetadata.Builder().setTitle("Tengo Fe").setArtist("Feid").build()
        val mediaItem = MediaItem.Builder()
            .setMediaMetadata(mediaMetadata).setUri(trackUrl).build()
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
        _isPlaying.value = true
        _currentTrack.value = trackUrl
        listener?.onTrackPlaying(trackUrl) // Notify the listener
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
    }

    fun seekToPrevious() {
        player?.seekToPrevious()
    }

    fun setListener(listener: ExoPlayerListener) {
        this.listener = listener
    }

    fun getCurrentTrack(): String? {
        return _currentTrack.value
    }

    fun getSongTitle():String{
        return player?.mediaMetadata?.title.toString()

    }

    override fun onCleared() {
        super.onCleared()
        player?.release()
        player = null
    }

    interface ExoPlayerListener {
        fun onTrackPlaying(trackUrl: String)
    }
}