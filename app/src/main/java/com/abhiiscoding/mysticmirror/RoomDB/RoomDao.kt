package com.abhiiscoding.mysticmirror.RoomDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
abstract class NotesDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertNote(note: Note)

    @Query("SELECT * FROM notes_table")
    abstract suspend fun getAllNotes(): List<Note>

}