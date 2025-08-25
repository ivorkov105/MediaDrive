package com.example.mediadrive.utils

import com.example.mediadrive.R

sealed class Screen(
    val route: String,
    val label: String,
    val icon: Int
) {
    object AudioGraph: Screen("audio_graph", "Аудио", R.drawable.ic_audio)
    object Video: Screen("video", "Видео", R.drawable.ic_video)
    object Photo: Screen("photo", "Фото", R.drawable.ic_photo)
    object AudioList: Screen("audio_list", "Список аудио", R.drawable.ic_audio)
    object AudioPlayer: Screen("audio_player/{audioIndex}", "АудиоПлеер", R.drawable.ic_audio) {
        fun createRoute(audioIndex: Int) = "audio_player/$audioIndex"
    }
}
