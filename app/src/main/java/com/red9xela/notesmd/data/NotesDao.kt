package com.red9xela.notesmd.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Query("DELETE FROM notes WHERE id == :noteId")
    suspend fun deleteNote(noteId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNote(noteDbModel: NoteDbModel)

    @Query("SELECT * FROM notes ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteDbModel>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNote(noteId: Int): NoteDbModel

    @Query(
        """SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' 
        || :query || '%' ORDER BY isPinned DESC, updatedAt DESC"""
    )
    fun searchNotes(query: String): Flow<List<NoteDbModel>>

    @Query("UPDATE notes SET isPinned = NOT isPinned WHERE id == :noteId")
    fun switchPinnedStatus(noteId: Int)
}