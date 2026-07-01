package com.red9xela.notesmd.di

import android.content.Context
import androidx.room.Room
import com.red9xela.notesmd.data.NotesDao
import com.red9xela.notesmd.data.NotesDatabase
import com.red9xela.notesmd.data.NotesRepositoryImpl
import com.red9xela.notesmd.domain.NotesRepository
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
    fun bindNotesRepository(
        impl: NotesRepositoryImpl
    ): NotesRepository

    companion object {
        @Singleton
        @Provides
        fun provideDatabase(
            @ApplicationContext context: Context
        ): NotesDatabase {
            return Room.databaseBuilder(
                context = context,
                klass = NotesDatabase::class.java,
                name = "notes.db"
            ).fallbackToDestructiveMigration(dropAllTables = true).build()
        }

        @Singleton
        @Provides
        fun providesNotesDao(database: NotesDatabase): NotesDao {
            return database.notesDao()
        }
    }

}