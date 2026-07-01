package com.red9xela.notesmd.data

import com.red9xela.notesmd.domain.Note

fun Note.toNoteDbModel(): NoteDbModel {
    return NoteDbModel(
        id = id,
        title = title,
        content = markdownContent,
        updatedAt = updatedAt,
        isPinned = isPinned
    )
}

fun NoteDbModel.toEntity(): Note {
    return Note(
        id = id,
        title = title,
        markdownContent = content,
        updatedAt = updatedAt,
        isPinned = isPinned
    )
}

fun List<NoteDbModel>.toEntities(): List<Note> {
    return this.map { it.toEntity() }
}

fun List<Note>.toItemDbModels(): List<NoteDbModel> {
    return this.map { it.toNoteDbModel() }
}