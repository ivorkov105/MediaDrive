package com.example.mediadrive.ui.composables.audio.player

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mediadrive.ui.customviews.CustomAudioPlayerView

@Composable
fun AudioPlayerScreen(
    viewModel: AudioPlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val albumArtBitmap = remember {
        null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize(),
            factory = { ctx ->
                CustomAudioPlayerView(ctx).apply {
                    setOnAudioPlayerListener(object : CustomAudioPlayerView.AudioPlayerListener {
                        override fun onPlayPauseClicked() {
                            viewModel.onPlayPauseClicked()
                        }

                        override fun onNextClicked() {
                            viewModel.onNextClicked()
                        }

                        override fun onPrevClicked() {
                            viewModel.onPrevClicked()
                        }

                        override fun onTimelineChanged(newProgress: Float) {
                            viewModel.onTimelineChanged(newProgress)
                        }
                    })
                }
            },
            update = { view ->
                Log.d("AudioIndex", uiState.index.toString())
                view.setIsPlaying(uiState.isPlaying)
                view.setProgress(uiState.progress)
                view.setTrackInfo(
                    uiState.trackTitle ?: "Unknown Track",
                    uiState.trackAuthor ?: "Unknown Author"
                )
                view.setAlbumArt(albumArtBitmap)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AudioPlayerScreen_prev() {
    AudioPlayerScreen()
}