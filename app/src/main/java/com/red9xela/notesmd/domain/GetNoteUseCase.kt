package com.red9xela.notesmd.domain

import javax.inject.Inject

class GetNoteUseCase @Inject constructor(private val repository: NotesRepository)  {
    suspend operator fun invoke(id: Int) : Note {
       return repository.getNote(noteId = id)
    }
}