package com.singletonv.notes.data

import com.singletonv.notes.domain.Note

fun Note.toDbModel(): NoteDbModel {
    return NoteDbModel(id, title, content, updatedAt, isPinned)
}

fun NoteDbModel.toEntity(): Note {
    return Note(id, title, content, updatedAt, isPinned)
}

fun List<NoteDbModel>.toEntities(): List<Note> {
    return map { it.toEntity() }
}