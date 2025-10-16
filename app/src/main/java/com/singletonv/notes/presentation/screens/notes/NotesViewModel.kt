package com.singletonv.notes.presentation.screens.notes

import androidx.lifecycle.ViewModel
import com.singletonv.notes.data.TestNotesRepositoryImpl
import com.singletonv.notes.domain.AddNoteUseCase
import com.singletonv.notes.domain.DeleteNoteUseCase
import com.singletonv.notes.domain.EditNoteUseCase
import com.singletonv.notes.domain.GetAllNotesUseCase
import com.singletonv.notes.domain.GetNoteUseCase
import com.singletonv.notes.domain.Note
import com.singletonv.notes.domain.SearchNotesUseCase
import com.singletonv.notes.domain.SwitchPinnedStatusUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModel : ViewModel() {

    private val repository = TestNotesRepositoryImpl

    private val addNoteUseCase = AddNoteUseCase(repository)
    private val deleteNoteUseCase = DeleteNoteUseCase(repository)
    private val editNoteUseCase = EditNoteUseCase(repository)
    private val getAllNotesUseCase = GetAllNotesUseCase(repository)
    private val getNoteUseCase = GetNoteUseCase(repository)
    private val searchNotesUseCase = SearchNotesUseCase(repository)
    private val switchPinnedStatusUseCase = SwitchPinnedStatusUseCase(repository)

    private val query = MutableStateFlow("")

    private val _state = MutableStateFlow(NotesScreenState())
    val state = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        addSomeNotes()
        query
            .onEach { input ->
                _state.update { it.copy(query = input) }
            }
            .flatMapLatest {
                if (it.isBlank()) {
                    getAllNotesUseCase()
                } else {
                    searchNotesUseCase(it.trim())
                }
            }
            .onEach { notes ->
                _state.update { state ->
                    val (pinnedNotes, otherNotes) = notes.partition { it.isPinned }
                    state.copy(
                        pinnedNotes = pinnedNotes,
                        otherNotes = otherNotes
                    )
                }
            }
            .launchIn(scope)
    }

    // TODO: remove after full implementation
    private fun addSomeNotes() {
        repeat(50) {
            addNoteUseCase(
                title = "Title #$it",
                content = "Content #$it"
            )
        }
    }

    fun processCommand(command: NotesCommand) {
        when (command) {
            is NotesCommand.DeleteNote -> deleteNoteUseCase(command.noteId)
            is NotesCommand.EditNote -> {
                val note = getNoteUseCase(command.note.id) // TODO: remove after checking use case
                val oldTitle = note.title
                val newTitle = "$oldTitle edited"
                editNoteUseCase(note.copy(title = newTitle))
            }
            is NotesCommand.InputSearchQuery -> query.update { command.query }
            is NotesCommand.SwitchPinnedStatus -> switchPinnedStatusUseCase(command.noteId)
        }
    }
}

sealed interface NotesCommand {

    data class InputSearchQuery(val query: String) : NotesCommand
    data class SwitchPinnedStatus(val noteId: Int) : NotesCommand

    // Temp
    data class DeleteNote(val noteId: Int) : NotesCommand
    data class EditNote(val note: Note) : NotesCommand
}

data class NotesScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val otherNotes: List<Note> = listOf()
)