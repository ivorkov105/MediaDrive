package com.example.mediadrive.domain.repos

import com.example.mediadrive.data.models.AudioFile
import com.example.mediadrive.domain.entities.AudioUiState
import kotlinx.coroutines.flow.Flow

interface AudioPlayerRepository {
    fun getPlayerState(): Flow<AudioUiState>
    fun play()
    fun playAudio(index: Int)
    fun pause()
    fun seekTo(progress: Float)
    fun seekToNext()
    fun seekToPrevious()
    fun release()
    suspend fun setPlaylist(playlist: List<AudioFile>)
}