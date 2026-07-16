package com.red9xela.notesmd.data

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.red9xela.notesmd.domain.Note
import com.red9xela.notesmd.domain.NotesRepository
import com.red9xela.notesmd.domain.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val notesDao: NotesDao,
    private val settingsRepository: SettingsRepository,
    @param:ApplicationContext private val context: Context
) : NotesRepository {

    override suspend fun deleteNote(id: Int) {
        val note = getNote(id)

        val folderUriString = settingsRepository.folderUri.first() ?: return
        val rootFolder = DocumentFile.fromTreeUri(context, folderUriString.toUri())
        rootFolder?.findFile("${note.title}.md")?.delete()

        notesDao.deleteNote(noteId = id)
    }

    override suspend fun saveNote(note: Note) {
        val safeTitle = note.title.replace(Regex("[\\\\/:*?\"<>|]"), "_")
        val noteToSave = note.copy(title = safeTitle)

        if (note.id != 0) {
            val oldNote = getNote(note.id)
            if (oldNote.title != noteToSave.title) {
                val folderUriString = settingsRepository.folderUri.first() ?: ""
                if (folderUriString.isNotEmpty()) {
                    val rootFolder = DocumentFile.fromTreeUri(context, folderUriString.toUri())
                    rootFolder?.findFile("${oldNote.title}.md")?.delete()
                }
            }
        }
        writeNoteToFile(noteToSave)

        notesDao.saveNote(noteToSave.toNoteDbModel())
    }

    private suspend fun writeNoteToFile(note: Note): Uri? {
        val folderUriString = settingsRepository.folderUri.first() ?: return null
        val rootFolder = DocumentFile.fromTreeUri(context, folderUriString.toUri()) ?: return null

        val safeTitle = note.title.replace(Regex("[\\\\/:*?\"<>|]"), "_")
        val fileName = "$safeTitle.md"

        val file = rootFolder.findFile(fileName) ?: rootFolder.createFile("text/markdown", fileName)

        return file?.uri?.also { uri ->
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(note.markdownContent.toByteArray())
            }
        }
    }

    override suspend fun syncNotesFromFolder() {
        val folderUriString = settingsRepository.folderUri.first() ?: return
        val rootFolder = DocumentFile.fromTreeUri(context, folderUriString.toUri()) ?: return
        val files = rootFolder.listFiles().filter { it.name?.endsWith(".md", ignoreCase = true) == true }
        val fileTitles = files.map { it.name?.substringBeforeLast(".") }

        val currentDbNotes = notesDao.getAllNotes().first()
        currentDbNotes.forEach { dbNote ->
            if (dbNote.title !in fileTitles) {
                notesDao.deleteNote(dbNote.id)
            }
        }

        files.forEach { file ->
            val title = file.name?.substringBeforeLast(".") ?: return@forEach
            val existing = currentDbNotes.find { it.title == title }

            if (existing == null || existing.updatedAt != file.lastModified()) {
                val content = readFileContent(file.uri)
                notesDao.saveNote(
                    NoteDbModel(
                        id = existing?.id ?: 0,
                        title = title,
                        content = content,
                        updatedAt = file.lastModified(),
                        isPinned = existing?.isPinned ?: false
                    )
                )
            }
        }
    }

    private fun readFileContent(uri: Uri): String {
        return context.contentResolver.openInputStream(uri)?.use { stream ->
            stream.bufferedReader().readText()
        } ?: ""
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getAllNotes().map { it.toEntities() }
    }

    override suspend fun getNote(noteId: Int): Note {
        return notesDao.getNote(noteId = noteId).toEntity()
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesDao.searchNotes(query).map { it.toEntities() }
    }

    override suspend fun switchPinnedStatus(noteId: Int) {
        notesDao.switchPinnedStatus(noteId)
    }
}