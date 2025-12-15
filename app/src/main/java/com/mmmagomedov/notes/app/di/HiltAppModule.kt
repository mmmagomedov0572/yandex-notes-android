package com.mmmagomedov.notes.app.di

import android.content.Context
import com.mmmagomedov.notes.data.datasource.FakeNotesRemoteDataSource
import com.mmmagomedov.notes.data.datasource.FileNotebook
import com.mmmagomedov.notes.data.repository.SyncNotesRepositoryImpl
import com.mmmagomedov.notes.domain.datasource.NotesLocalDataSource
import com.mmmagomedov.notes.domain.datasource.NotesRemoteDataSource
import com.mmmagomedov.notes.domain.repository.SyncNotesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltAppModule {

    @Provides
    @Singleton
    fun provideNotesLocalDataSource(
        @ApplicationContext context: Context
    ): NotesLocalDataSource = FileNotebook(context)

    @Provides
    @Singleton
    fun provideNotesRemoteDataSource(): NotesRemoteDataSource = FakeNotesRemoteDataSource()

    @Provides
    @Singleton
    fun provideNotesRepository(
        local: NotesLocalDataSource,
        remote: NotesRemoteDataSource
    ): SyncNotesRepository = SyncNotesRepositoryImpl(local, remote)

}