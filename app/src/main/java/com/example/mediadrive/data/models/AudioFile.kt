package com.example.mediadrive.data.models

import android.net.Uri

data class AudioFile(
    val id: Long,
    val uri: Uri,
    val name: String,
    val artist: String,
    val duration: Int
)