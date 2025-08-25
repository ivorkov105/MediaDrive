package com.example.mediadrive.domain.entities

import androidx.media3.common.Player

data class AudioUiState(
    val isPlaying: Boolean = false,
    val progress: Float = 0.0f,
    val trackTitle: String? = "Unknown title",
    val trackAuthor: String? = "Unknown author",
    val duration: Long = 0,
    val currentPosition: Long = 0,
    val index: Int = -1,
    val playbackState: Int = Player.STATE_IDLE
)