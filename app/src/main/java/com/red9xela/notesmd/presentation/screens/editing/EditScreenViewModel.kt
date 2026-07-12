package com.red9xela.notesmd.presentation.screens.editing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.red9xela.notesmd.domain.DeleteNoteUseCase
import com.red9xela.notesmd.domain.GetNoteUseCase
import com.red9xela.notesmd.domain.EditNotesUseCase
import com.red9xela.notesmd.domain.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EditScreenViewModel @Inject constructor(
    private val editNotesUseCase: EditNotesUseCase,
    private val getNoteUseCase: GetNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val noteId = savedStateHandle.get<Int>("note_id") ?: -1

    private val isNewNote: Boolean = noteId == -1
    private val _state = MutableStateFlow(EditNoteState())
    val state = _state.asStateFlow()

    init {
        if (!isNewNote) {
            loadNote()
        }
    }

    private fun loadNote() {
        viewModelScope.launch {
            val note = getNoteUseCase(noteId)

            _state.update {
                it.copy(
                    noteId = note.id,
                    title = note.title,
                    markdownContent = note.markdownContent,
                    updatedAt = note.updatedAt,
                    isPinned = note.isPinned,
                )
            }
        }
    }

    fun processCommand(command: EditNoteCommand) {
        when (command) {
            EditNoteCommand.Back -> _state.update {
                it.copy(isFinished = true)
            }

            EditNoteCommand.Delete -> {
                if (isNewNote) {
                    _state.update {
                        it.copy(isFinished = true)
                    }
                } else {
                    viewModelScope.launch {
                        deleteNoteUseCase(id = state.value.noteId)
                        _state.update {
                            it.copy(isFinished = true)
                        }
                    }
                }
            }

            EditNoteCommand.Save -> {
                viewModelScope.launch {
                    val currentState = state.value
                    editNotesUseCase(
                        Note(
                            id = if (isNewNote) 0 else currentState.noteId,
                            title = currentState.title,
                            markdownContent = currentState.markdownContent,
                            updatedAt = System.currentTimeMillis(),
                            isPinned = currentState.isPinned
                        )
                    )
                    _state.update { it.copy(isFinished = true) }
                }
            }

            is EditNoteCommand.InputContent -> {
                _state.update {
                    it.copy(markdownContent = command.contentMarkdown)
                }
            }

            is EditNoteCommand.InputTitle -> {
                _state.update {
                    it.copy(title = command.title)
                }
            }

            EditNoteCommand.ChangeMode -> {
                _state.update {
                    it.copy(isEditMode = !it.isEditMode)
                }
            }
        }

    }
}

data class EditNoteState(
    val noteId: Int = 0,
    val title: String = "",
    val markdownContent: String = "",
    val updatedAt: Long = 0L,
    val isPinned: Boolean = false,
    val isFinished: Boolean = false,
    val isEditMode: Boolean = true
) {
    val isSaveEnabled: Boolean
        get() = title.isNotBlank() &&
                markdownContent.isNotBlank()
}

sealed interface EditNoteCommand {
    data class InputTitle(val title: String) : EditNoteCommand
    data class InputContent(val contentMarkdown: String) : EditNoteCommand
    data object Save : EditNoteCommand
    data object Back : EditNoteCommand
    data object Delete : EditNoteCommand
    data object ChangeMode : EditNoteCommand
}


