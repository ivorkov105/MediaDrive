package com.example.mediadrive.data.datasources

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.database.Cursor
import android.net.Uri
import com.example.mediadrive.data.models.AudioFile

fun getAudioFilesFromLocalStorage(context: Context): List<AudioFile> {
        val audioFileFiles = mutableListOf<AudioFile>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        val contentResolver = context.contentResolver
        val cursor: Cursor? = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val name = it.getString(nameColumn)
                val artist = it.getString(artistColumn)
                val duration = it.getInt(durationColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                audioFileFiles.add(
                    AudioFile(
                    id = id,
                    name = name,
                    artist = artist,
                    uri = contentUri,
                    duration = duration)
                )
            }
        }
        return audioFileFiles
    }