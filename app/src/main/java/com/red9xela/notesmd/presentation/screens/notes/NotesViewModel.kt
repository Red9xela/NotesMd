@file:OptIn(ExperimentalCoroutinesApi::class)

package com.red9xela.notesmd.presentation.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.net.Uri
import com.red9xela.notesmd.domain.GetAllNotesUseCase
import com.red9xela.notesmd.domain.Note
import com.red9xela.notesmd.domain.SearchNotesUseCase
import com.red9xela.notesmd.domain.SettingsRepository
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
    private val switchPinnedStatusUseCase: SwitchPinnedStatusUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val _state = MutableStateFlow(NotesScreenState())
    val state = _state.asStateFlow()

    init {
        settingsRepository.folderUri.onEach { uri ->
            _state.update { it.copy(folderUri = uri)
            }
        }.launchIn(viewModelScope)
    }

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

                val todayMillis = TimeUnit.DAYS.toMillis(1)
                val sevenDaysMillis = TimeUnit.DAYS.toMillis(7)
                val thirtyDaysMillis = TimeUnit.DAYS.toMillis(30)

                val unpinnedNotes = notes.filter { !it.isPinned }

                val todayNotes = unpinnedNotes.filter { note ->
                    val diff = now - note.updatedAt
                    diff < todayMillis
                }
                val previousSevenDays = unpinnedNotes.filter { note ->
                    val diff = now - note.updatedAt
                    diff in todayMillis until sevenDaysMillis
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
                        todayNotes = todayNotes,
                        previousSevenDays = previousSevenDays,
                        previousThirtyDays = previousThirtyDays,
                        olderNotes = olderNotes
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun saveFolderUri(uri: Uri) {
        viewModelScope.launch {
            settingsRepository.saveFolderUri(uri.toString())
        }
    }
    
    fun processCommand(command: NotesCommand) {
        viewModelScope.launch {
            when (command) {
                is NotesCommand.InputSearchQuery -> {
                    query.update { command.query }
                }

                is NotesCommand.SwitchPinnedStatus -> {
                    switchPinnedStatusUseCase(command.noteId)
                }

                is NotesCommand.SaveSelectedFolder -> {
                    saveFolderUri(uri = command.uri)
                }
            }
        }
    }
}

sealed interface NotesCommand {
    data class SwitchPinnedStatus(val noteId: Int) : NotesCommand
    data class InputSearchQuery(val query: String) : NotesCommand
    data class SaveSelectedFolder(val uri: Uri) : NotesCommand
}

data class NotesScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val todayNotes: List<Note> = listOf(),
    val previousSevenDays: List<Note> = listOf(),
    val previousThirtyDays: List<Note> = listOf(),
    val olderNotes: List<Note> = listOf(),
    val folderUri: String? = null
)
