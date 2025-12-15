package com.mmmagomedov.notes.app.di

import android.content.Context
import com.mmmagomedov.notes.data.FileNotebook
import com.mmmagomedov.notes.domain.repository.NotesRepository
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
    fun provideNotesRepository(
        @ApplicationContext context: Context
    ): NotesRepository = FileNotebook(context)

}