package com.red9xela.notesmd.data

import com.red9xela.notesmd.domain.Note
import com.red9xela.notesmd.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(private val notesDao: NotesDao) : NotesRepository {
    override suspend fun deleteNote(id: Int) {
        notesDao.deleteNote(noteId = id)
    }

    override suspend fun saveNote(note: Note) {
        notesDao.saveNote(note.toNoteDbModel())
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getAllNotes().map { it.toEntities() }
    }

    override suspend fun getNote(noteId: Int): Note? {
        return notesDao.getNote(noteId = noteId)?.toEntity()
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesDao.searchNotes(query).map { it.toEntities() }
    }

    override suspend fun switchPinnedStatus(noteId: Int) {
        notesDao.switchPinnedStatus(noteId)
    }
}