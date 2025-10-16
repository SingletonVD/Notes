package com.singletonv.notes.domain

class EditNoteUseCase(
    private val repository: NotesRepository
) {

    operator fun invoke(note: Note) {
        repository.editNote(note)
    }
}