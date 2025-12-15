package com.mmmagomedov.notes.app.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mmmagomedov.notes.data.api.NotesApi
import com.mmmagomedov.notes.data.datasource.BackendNotesRemoteDataSource
import com.mmmagomedov.notes.data.datasource.FileNotebook
import com.mmmagomedov.notes.data.datasource.LocalRoomDataSource
import com.mmmagomedov.notes.data.repository.SyncNotesRepositoryImpl
import com.mmmagomedov.notes.data.room.RoomNotesDatabase
import com.mmmagomedov.notes.domain.datasource.NotesLocalDataSource
import com.mmmagomedov.notes.domain.datasource.NotesRemoteDataSource
import com.mmmagomedov.notes.domain.datasource.TokenProvider
import com.mmmagomedov.notes.domain.model.Note
import com.mmmagomedov.notes.domain.repository.SyncNotesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltAppModule {

    private const val BASE_URL = "https://hive.mrdekk.ru/todo/"

    @Provides
    @Singleton
    fun provideTokenProvider(): TokenProvider = TokenProvider (
        token = "81c330c7-7072-4830-8036-a4ffd764cdcc"
    )

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideNotesApi(retrofit: Retrofit): NotesApi =
        retrofit.create(NotesApi::class.java)

//    @Provides
//    @Singleton
//    fun provideNotesLocalDataSource(
//        @ApplicationContext context: Context
//    ): NotesLocalDataSource = FileNotebook(context)

    @Provides
    @Singleton
    fun provideNotesRemoteDataSource(
        api: NotesApi,
        tokenProvider: TokenProvider
    ): NotesRemoteDataSource = BackendNotesRemoteDataSource(
        api = api,
        tokenProvider = tokenProvider
    )

    @Provides
    @Singleton
    fun provideLocalRoomDataSource(
        @ApplicationContext
        context: Context
    ): NotesLocalDataSource {
        return LocalRoomDataSource(
            noteDao = RoomNotesDatabase.getInstance(context).noteDao()
        )
    }

    @Provides
    @Singleton
    fun provideNotesRepository(
        local: NotesLocalDataSource,
        remote: NotesRemoteDataSource
    ): SyncNotesRepository = SyncNotesRepositoryImpl(local, remote)

}