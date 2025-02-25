package com.abhiiscoding.mysticmirror.RoomDB

import android.content.Context
import androidx.room.Room

object NotesGraph {
    lateinit var database: NotesDatabase

    val noteRepository by lazy {
        NoteRepository(database.notesDao())
    }

    fun provide(context: Context) {
        database = Room.databaseBuilder(
            context.applicationContext,
            NotesDatabase::class.java,
            "notes_database"
        ).build()
    }
}