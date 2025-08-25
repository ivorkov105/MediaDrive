package com.example.mediadrive.ui.composables.audio.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediadrive.domain.entities.AudioUiState
import com.example.mediadrive.domain.repos.AudioPlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    private val playerRepository: AudioPlayerRepository
): ViewModel() {

    val uiState: StateFlow<AudioUiState> = playerRepository.getPlayerState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = AudioUiState()
        )

    fun onPlayPauseClicked() {
        if (uiState.value.isPlaying) {
            playerRepository.pause()
        } else {
            playerRepository.play()
        }
    }

    fun playAudio(index: Int) {
        if (index != uiState.value.index) {
            playerRepository.playAudio(index)
        }
    }

    fun onTimelineChanged(newProgress: Float) {
        playerRepository.seekTo(newProgress)
    }

    fun onNextClicked() {
        playerRepository.seekToNext()
    }

    fun onPrevClicked() {
        playerRepository.seekToPrevious()
    }

    override fun onCleared() {
        playerRepository.release()
        super.onCleared()
    }
}