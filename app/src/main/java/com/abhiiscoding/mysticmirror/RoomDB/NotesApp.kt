package com.abhiiscoding.mysticmirror.RoomDB

import android.app.Application
import androidx.room.Room

class NotesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize your Room database first
        NotesGraph.database = Room.databaseBuilder(
            applicationContext,
            NotesDatabase::class.java,
            "notes_database"
        ).build()

        NotesGraph.provide(this)
    }
}
