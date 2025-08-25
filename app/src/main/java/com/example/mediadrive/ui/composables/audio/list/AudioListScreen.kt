package com.example.mediadrive.ui.composables.audio.list

import android.Manifest
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mediadrive.data.models.AudioFile
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AudioListScreen(
    onAudioClick: (audioIndex: Int) -> Unit,
    viewModel: AudioListViewModel = hiltViewModel()
) {
    val permissionState = rememberPermissionState(permission = Manifest.permission.READ_MEDIA_AUDIO)

    if (permissionState.status.isGranted) {
        LaunchedEffect(Unit) {
            viewModel.loadAudioFiles()
        }
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.audioFiles.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Аудиофайлы не найдены", style = MaterialTheme.typography.bodyLarge)
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(uiState.audioFiles) { index, file ->
                        AudioListItem(
                            audioFile = file,
                            onClick = {
                                onAudioClick(index)
                                Log.d("onClick", index.toString()
                                )
                            }
                        )
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Для доступа к аудиофайлам нужно разрешение.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = {
                permissionState.launchPermissionRequest()
            }) {
                Text("Запросить разрешение")
            }
        }
    }
}

@Composable
fun AudioListItem(audioFile: AudioFile, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(audioFile.name, maxLines = 1) },
        supportingContent = { Text(audioFile.artist, maxLines = 1) },
        modifier = Modifier.clickable(onClick = onClick)
    )
}