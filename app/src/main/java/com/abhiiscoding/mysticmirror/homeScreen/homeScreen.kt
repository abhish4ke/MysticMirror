package com.abhiiscoding.mysticmirror.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abhiiscoding.mysticmirror.RoomDB.Note
import com.abhiiscoding.mysticmirror.RoomDB.NotesViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(noteViewModel: NotesViewModel) {
    // Collect the list of notes from the ViewModel
    val notes by noteViewModel.allNotes.collectAsState()
    val scope = rememberCoroutineScope()
    // Holds the text input for the new note content.
    var noteContent by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(notes) { note ->
                Text(
                    text = note.title,
                    fontSize = 10.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = note.content,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Divider(
                    color = Color.Black,
                    thickness = 1.dp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        // BasicTextField for entering the new note content
        OutlinedTextField(
            value = noteContent,
            onValueChange = { noteContent = it },
            label = { Text("Enter note content") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textStyle = TextStyle(fontSize = 20.sp, color = Color.Black)
        )


        // Button to add the note using the content from the BasicTextField
        Button(
            onClick = {
                scope.launch {
                    if (noteContent.isNotEmpty()) {
                        noteViewModel.insertNote(Note(title = "Note:", content = noteContent))
                        noteContent = ""
                        noteViewModel.getAllNotes()
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(text = "Add Note")
        }
    }
}
