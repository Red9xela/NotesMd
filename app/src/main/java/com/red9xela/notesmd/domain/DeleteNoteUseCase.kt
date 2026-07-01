package com.red9xela.notesmd.domain

import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(private val repository: NotesRepository) {
    suspend operator fun invoke(id: Int) {
        repository.deleteNote(id = id)
    }
}