package com.example.mediadrive.data.repos_impl

import android.content.ComponentName
import android.content.Context
import androidx.annotation.OptIn
import androidx.concurrent.futures.await
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.mediadrive.data.models.AudioFile
import com.example.mediadrive.domain.entities.AudioUiState
import com.example.mediadrive.domain.repos.AudioPlayerRepository
import com.example.mediadrive.data.services.PlaybackService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPlayerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
): AudioPlayerRepository {

    private var mediaController: MediaController? = null

    @OptIn(UnstableApi::class)
    override fun getPlayerState(): Flow<AudioUiState> = callbackFlow {
        if (mediaController == null) {
            val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
            mediaController = MediaController.Builder(context, sessionToken).buildAsync().await()
        }

        val listener = object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                val duration = player.duration.coerceAtLeast(0)
                val position = player.currentPosition.coerceAtLeast(0)

                val playerState = AudioUiState(
                    isPlaying = player.isPlaying,
                    duration = duration,
                    currentPosition = position,
                    progress = if (duration > 0) position.toFloat() / duration else 0f,
                    trackTitle = player.mediaMetadata.title?.toString(),
                    trackAuthor = player.mediaMetadata.artist?.toString(),
                    index = player.currentMediaItemIndex,
                    playbackState = player.playbackState
                )
                trySend(playerState)
            }
        }

        mediaController?.addListener(listener)

        awaitClose {
            mediaController?.removeListener(listener)
        }
    }

    @OptIn(UnstableApi::class)
    override suspend fun setPlaylist(playlist: List<AudioFile>) {
        if (mediaController == null) {
            val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
            mediaController = MediaController.Builder(context, sessionToken).buildAsync().await()
        }

        val mediaItems = playlist.map { MediaItem.fromUri(it.uri) }
        mediaController?.setMediaItems(mediaItems)
        mediaController?.prepare()
        getPlayerState().first { it.playbackState == Player.STATE_READY }
    }

    override fun play() {
        mediaController?.play()
    }

    override fun playAudio(index: Int) {
        mediaController?.seekTo(index, 0L)
        mediaController?.play()
    }

    override fun pause() {
        mediaController?.pause()
    }

    override fun seekTo(progress: Float) {
        mediaController?.let {
            val newPosition = (it.duration * progress).toLong()
            it.seekTo(newPosition)
        }
    }

    override fun release() {
        MediaController.releaseFuture(mediaController?.let{
            MediaController.Builder(context, it.connectedToken!!).buildAsync()
        }!!)
        mediaController = null
    }

    override fun seekToNext() {
        mediaController?.seekToNextMediaItem()
    }

    override fun seekToPrevious() {
        mediaController?.seekToPreviousMediaItem()
    }
}