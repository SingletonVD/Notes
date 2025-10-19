package com.singletonv.notes.domain

import javax.inject.Inject

class AddNoteUseCase @Inject constructor(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(title: String, content: List<ContentItem>) {
        val filteredContent = content.filter {
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

        repository.addNote(
            title = title,
            content = filteredContent,
            isPinned = false,
            updatedAt = System.currentTimeMillis()
        )
    }
}