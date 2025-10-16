package com.singletonv.notes.data

import com.singletonv.notes.domain.Note
import com.singletonv.notes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

object TestNotesRepositoryImpl : NotesRepository {

    private val notesListFlow = MutableStateFlow<List<Note>>(listOf())

    override fun addNote(title: String, content: String) {
        notesListFlow.update { notes ->
            val note = Note(
                id = notes.size,
                title = title,
                content = content,
                updatedAt = System.currentTimeMillis(),
                isPinned = false
            )
            notes + note
        }
    }

    override fun deleteNote(noteId: Int) {
        notesListFlow.update { notes -> notes.filterNot { it.id == noteId } }
    }

    override fun editNote(note: Note) {
        notesListFlow.update { notes -> notes.map {
            if (it.id == note.id) note
            else it
        } }
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesListFlow.asStateFlow()
    }

    override fun getNote(noteId: Int): Note {
        return notesListFlow.value.first { it.id == noteId }
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesListFlow
            .map { notes ->
                notes
                    .filter {
                        it.title.contains(query) || it.content.contains(query)
                    }
            }

    }

    override fun switchPinStatus(noteId: Int) {
        notesListFlow.value.map {
            if (it.id == noteId) it.copy(isPinned = !it.isPinned)
            else it
        }
    }
}