package com.example.mediadrive.domain.repos

import com.example.mediadrive.data.models.AudioFile
import kotlinx.coroutines.flow.Flow

interface AudioRepository {
    suspend fun getAudioFiles(): Flow<List<AudioFile>>
}