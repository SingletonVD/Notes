package com.singletonv.notes.data

import com.singletonv.notes.domain.ContentItem
import com.singletonv.notes.domain.Note
import kotlinx.serialization.json.Json

fun Note.toDbModel(): NoteDbModel {
    val contentAsString = Json.encodeToString(content.toContentItemDbModels())
    return NoteDbModel(id, title, contentAsString, updatedAt, isPinned)
}

fun NoteDbModel.toEntity(): Note {
    val content = Json
        .decodeFromString<List<ContentItemDbModel>>(content)
        .toContentItems()
    return Note(id, title, content, updatedAt, isPinned)
}

fun List<NoteDbModel>.toEntities(): List<Note> {
    return map { it.toEntity() }
}

fun List<ContentItem>.toContentItemDbModels(): List<ContentItemDbModel> {
    return map { contentItem ->
        when (contentItem) {
            is ContentItem.Image -> ContentItemDbModel.Image(contentItem.url)
            is ContentItem.Text -> ContentItemDbModel.Text(contentItem.content)
        }
    }
}

fun List<ContentItemDbModel>.toContentItems(): List<ContentItem> {
    return map { contentItem ->
        when (contentItem) {
            is ContentItemDbModel.Image -> ContentItem.Image(contentItem.url)
            is ContentItemDbModel.Text -> ContentItem.Text(contentItem.content)
        }
    }
}