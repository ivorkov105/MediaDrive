package com.example.mediadrive.ui.composables.audio.list

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.mediadrive.domain.entities.AudioListUiState
import com.example.mediadrive.domain.repos.AudioPlayerRepository
import com.example.mediadrive.domain.usecases.GetAudioFilesUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class AudioListViewModel @Inject constructor(
    private val getAudioFilesUseCase: GetAudioFilesUseCase,
    private val playerRepository: AudioPlayerRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(AudioListUiState())
    val uiState = _uiState.asStateFlow()

    fun loadAudioFiles() {
        Log.d("LoadAudioFiles","")
        viewModelScope.launch {
            _uiState.value = AudioListUiState(isLoading = true)
            getAudioFilesUseCase().collectLatest { files ->
                playerRepository.setPlaylist(files)
                _uiState.value = AudioListUiState(isLoading = false, audioFiles = files)
            }
        }
    }
}