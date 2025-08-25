package com.example.mediadrive.data.services

import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint

@UnstableApi
@AndroidEntryPoint
class PlaybackService: MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private var lastPlayedMediaItem: MediaItem? = null

    private inner class CustomCallback : MediaSession.Callback {

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<MutableList<MediaItem>> {
            lastPlayedMediaItem = mediaItems.firstOrNull()
            return super.onAddMediaItems(mediaSession, controller, mediaItems)
        }

        override fun onPlaybackResumption(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo
        ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {

            val mediaItemToPlay = lastPlayedMediaItem ?: mediaSession.player.currentMediaItem

            if (mediaItemToPlay == null) {
                val emptyResult = MediaSession.MediaItemsWithStartPosition(emptyList(), 0, 0)
                return Futures.immediateFuture(emptyResult)
            }

            val result = MediaSession.MediaItemsWithStartPosition(
                listOf(mediaItemToPlay),
                0,
                0L
            )
            return Futures.immediateFuture(result)
        }
    }


    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player)
            .setCallback(CustomCallback())
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}