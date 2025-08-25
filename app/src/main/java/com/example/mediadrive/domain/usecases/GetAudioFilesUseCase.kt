package com.example.mediadrive.domain.usecases

import com.example.mediadrive.domain.repos.AudioRepository
import javax.inject.Inject

class GetAudioFilesUseCase @Inject constructor(
    private val repository: AudioRepository
) {
    suspend operator fun invoke() = repository.getAudioFiles()
}