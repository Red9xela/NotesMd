package com.red9xela.notesmd.domain

import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    suspend fun deleteNote(id: Int)

    suspend fun saveNote(note: Note)

    fun getAllNotes(): Flow<List<Note>>

    suspend fun getNote(noteId: Int): Note?

    fun searchNotes(query: String): Flow<List<Note>>

    suspend fun switchPinnedStatus(noteId: Int)
}