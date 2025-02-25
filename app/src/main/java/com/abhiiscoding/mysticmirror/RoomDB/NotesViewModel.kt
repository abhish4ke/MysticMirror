package com.abhiiscoding.mysticmirror.RoomDB

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotesViewModel(private val noteRepository: NoteRepository = NotesGraph.noteRepository) : ViewModel(){

    private var _allNotes = MutableStateFlow<List<Note>>(emptyList())
    val allNotes: StateFlow<List<Note>> = _allNotes


    init {
        viewModelScope.launch{
            getAllNotes()
        }
    }

    suspend fun insertNote(note: Note) {
        noteRepository.insertNote(note)
    }

    suspend fun getAllNotes(): List<Note> {
        _allNotes.value = noteRepository.getAllNotes()
        return noteRepository.getAllNotes()
    }

}