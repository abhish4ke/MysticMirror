package com.abhiiscoding.mysticmirror.RoomDB

import android.util.Log

class NoteRepository(private val notesDao: NotesDao) {
    suspend fun insertNote(note: Note) {
        notesDao.insertNote(note)
        Log.d("NoteRepository", "Note inserted: $note")
    }

    suspend fun getAllNotes(): List<Note> {
        return notesDao.getAllNotes()
    }
}