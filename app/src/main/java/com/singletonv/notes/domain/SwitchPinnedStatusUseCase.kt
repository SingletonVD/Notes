package com.singletonv.notes.domain

class SwitchPinnedStatusUseCase(
    private val repository: NotesRepository
) {

    operator fun invoke(noteId: Int) {
        repository.switchPinStatus(noteId)
    }
}