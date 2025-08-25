package com.example.mediadrive.domain.entities

import com.example.mediadrive.data.models.AudioFile

data class AudioListUiState(
    val isLoading: Boolean = true,
    val audioFiles: List<AudioFile> = emptyList()
)