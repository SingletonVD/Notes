package com.singletonv.notes.di

import android.content.Context
import com.singletonv.notes.data.NotesDatabase
import com.singletonv.notes.data.NotesRepositoryImpl
import com.singletonv.notes.domain.NotesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun provideRepository(
        impl: NotesRepositoryImpl
    ): NotesRepository

    companion object {

        @Singleton
        @Provides
        fun provideNotesDatabase(@ApplicationContext context: Context): NotesDatabase {
            return NotesDatabase.Companion.getInstance(context)
        }
    }
}