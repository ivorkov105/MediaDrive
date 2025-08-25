package com.example.mediadrive.di

import com.example.mediadrive.data.repos_impl.AudioPlayerRepositoryImpl
import com.example.mediadrive.data.repos_impl.AudioRepositoryImpl
import com.example.mediadrive.domain.repos.AudioPlayerRepository
import com.example.mediadrive.domain.repos.AudioRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindPlayerRepository(
        playerRepositoryImpl: AudioPlayerRepositoryImpl
    ): AudioPlayerRepository

    @Binds
    @Singleton
    abstract fun bindLocalAudioRepository(
        localAudioRepositoryImpl: AudioRepositoryImpl
    ): AudioRepository
}