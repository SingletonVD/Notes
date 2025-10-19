package com.singletonv.notes.data

import com.singletonv.notes.domain.ContentItem
import com.singletonv.notes.domain.Note
import com.singletonv.notes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val notesDao: NotesDao,
    private val imageFileManager: ImageFileManager
) : NotesRepository {

    override suspend fun addNote(
        title: String,
        content: List<ContentItem>,
        isPinned: Boolean,
        updatedAt: Long
    ) {
        val processedContent = content.processForStorage()
        val note = Note(0, title, processedContent, updatedAt, isPinned)
        val noteDbModel = note.toDbModel()
        notesDao.addNote(noteDbModel)
    }

    override suspend fun deleteNote(noteId: Int) {
        val note = getNote(noteId)

        note.content.filterIsInstance<ContentItem.Image>().forEach {
            imageFileManager.deleteImage(it.url)
        }

        notesDao.deleteNote(noteId)
    }

    override suspend fun editNote(note: Note) {
        val oldNote = getNote(note.id)
        val oldUrls = oldNote.content.filterIsInstance<ContentItem.Image>().map { it.url }
        val newUrls = note.content.filterIsInstance<ContentItem.Image>().map { it.url }
        val removedUrls = oldUrls - newUrls

        removedUrls.forEach {
            imageFileManager.deleteImage(it)
        }

        val processedContent = note.content.processForStorage()
        notesDao.addNote(note.copy(content = processedContent).toDbModel())
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getAllNotes().map { it.toEntities() }
    }

    override suspend fun getNote(noteId: Int): Note {
        return notesDao.getNote(noteId).toEntity()
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesDao.searchNotes(query).map { it.toEntities() }
    }

    override suspend fun switchPinStatus(noteId: Int) {
        notesDao.switchPinnedStatus(noteId)
    }

    private suspend fun List<ContentItem>.processForStorage(): List<ContentItem> {
        return map { contentItem ->
            when (contentItem) {
                is ContentItem.Text -> contentItem
                is ContentItem.Image ->
                    if (imageFileManager.isInternal(contentItem.url)) {
                        contentItem
                    } else {
                        imageFileManager.copyImageToInternalStorage(contentItem.url).let {
                            ContentItem.Image(it)
                        }
                    }
            }
        }
    }
}