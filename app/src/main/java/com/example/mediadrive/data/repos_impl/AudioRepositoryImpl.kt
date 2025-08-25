package com.example.mediadrive.data.repos_impl

import android.content.Context
import com.example.mediadrive.data.datasources.getAudioFilesFromLocalStorage
import com.example.mediadrive.data.models.AudioFile
import com.example.mediadrive.domain.repos.AudioRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AudioRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
): AudioRepository {
    override suspend fun getAudioFiles(): Flow<List<AudioFile>> = flow {
        val audioFiles = getAudioFilesFromLocalStorage(context)
        emit(audioFiles)
    }
}