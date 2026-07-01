package com.red9xela.notesmd.domain

import javax.inject.Inject

class SwitchPinnedStatusUseCase @Inject constructor(private val repository: NotesRepository) {
    suspend operator fun invoke(noteId: Int) {
        repository.switchPinnedStatus(noteId = noteId)
    }
}