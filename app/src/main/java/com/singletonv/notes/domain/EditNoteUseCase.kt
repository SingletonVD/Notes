package com.singletonv.notes.domain

import javax.inject.Inject

class EditNoteUseCase @Inject constructor(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(note: Note) {
        val filteredContent = note.content.filter {
            when (it) {
                is ContentItem.Image -> true
                is ContentItem.Text -> it.content.isNotBlank()
            }
        }.map {
            when (it) {
                is ContentItem.Image -> it
                is ContentItem.Text -> it.copy(content = it.content.trim())
            }
        }

        repository.editNote(
            note.copy(
                content = filteredContent,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}