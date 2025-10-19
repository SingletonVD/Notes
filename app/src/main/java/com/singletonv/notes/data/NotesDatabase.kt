package com.singletonv.notes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [NoteDbModel::class, ContentItemDbModel::class],
    version = 3,
    exportSchema = false
)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun notesDao(): NotesDao

    companion object {
        private var instance: NotesDatabase? = null
        private val LOCK = Any()

        fun getInstance(context: Context): NotesDatabase {

            instance?.let { return it }

            synchronized(LOCK) {
                instance?.let { return it }
                return Room.databaseBuilder(
                    context = context,
                    klass = NotesDatabase::class.java,
                    name = "notes.db"
                ).fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also {
                        instance = it
                    }
            }
        }
    }
}