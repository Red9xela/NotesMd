@file:OptIn(ExperimentalCoroutinesApi::class)

package com.red9xela.notesmd.presentation.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.red9xela.notesmd.domain.GetAllNotesUseCase
import com.red9xela.notesmd.domain.Note
import com.red9xela.notesmd.domain.SearchNotesUseCase
import com.red9xela.notesmd.domain.SwitchPinnedStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val searchNotesUseCase: SearchNotesUseCase,
    private val switchPinnedStatusUseCase: SwitchPinnedStatusUseCase
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val _state = MutableStateFlow(NotesScreenState())
    val state = _state.asStateFlow()


    init {
        query
            .onEach { input ->
                _state.update {
                    it.copy(query = input)
                }
            }
            .flatMapLatest { input ->
                if (input.isBlank()) {
                    getAllNotesUseCase()
                } else {
                    searchNotesUseCase(query = input.trim())
                }
            }
            .onEach { notes ->
                val now = System.currentTimeMillis()
                val pinnedNotes = notes.filter { it.isPinned }

                val sevenDaysMillis = TimeUnit.DAYS.toMillis(7)
                val thirtyDaysMillis = TimeUnit.DAYS.toMillis(30)

                val unpinnedNotes = notes.filter { !it.isPinned }

                val previousSevenDays = unpinnedNotes.filter { note ->
                    val diff = now - note.updatedAt
                    diff < sevenDaysMillis
                }
                val previousThirtyDays = unpinnedNotes.filter { note ->
                    val diff = now - note.updatedAt
                    diff in sevenDaysMillis until thirtyDaysMillis
                }

                val olderNotes = unpinnedNotes.filter { note ->
                    val diff = now - note.updatedAt
                    diff >= thirtyDaysMillis
                }

                _state.update {
                    it.copy(
                        pinnedNotes = pinnedNotes,
                        previousSevenDays = previousSevenDays,
                        previousThirtyDays = previousThirtyDays,
                        olderNotes = olderNotes
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    //
    fun processCommand(command: NotesCommand) {
        viewModelScope.launch {
            when (command) {
                is NotesCommand.InputSearchQuery -> {
                    query.update { command.query }
                }

                is NotesCommand.SwitchPinnedStatus -> {
                    switchPinnedStatusUseCase(command.noteId)
                }
            }
        }

    }
}

sealed interface NotesCommand {
    data class SwitchPinnedStatus(val noteId: Int) : NotesCommand
    data class InputSearchQuery(val query: String) : NotesCommand
}

data class NotesScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val previousSevenDays: List<Note> = listOf(),
    val previousThirtyDays: List<Note> = listOf(),
    val olderNotes: List<Note> = listOf(),
)
