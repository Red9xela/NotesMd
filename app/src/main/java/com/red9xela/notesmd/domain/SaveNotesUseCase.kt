package com.red9xela.notesmd.domain

import javax.inject.Inject

class SaveNotesUseCase @Inject constructor(private val repository: NotesRepository) {
    suspend operator fun invoke(note: Note) {
        repository.saveNote(note = note)
    }
}