package com.singletonv.notes.presentation.screens.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.singletonv.notes.domain.Note

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = modifier
            .padding(top = 48.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = state.pinnedNotes,
                    key = { it.id }
                ) { note ->
                    NoteCard(
                        note = note,
                        onNoteClick = {
                            viewModel.processCommand(
                                command = NotesCommand.SwitchPinnedStatus(it.id)
                            )
                        }
                    )
                }
            }
        }


        items(
            items = state.otherNotes,
            key = { it.id }
        ) { note ->
            NoteCard(
                note = note,
                onNoteClick = {
                    viewModel.processCommand(
                        command = NotesCommand.SwitchPinnedStatus(it.id)
                    )
                }
            )
        }
    }
}

@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    onNoteClick: (Note) -> Unit = {}
) {
    Text(
        modifier = modifier.clickable {
            onNoteClick(note)
        },
        text = "${note.title} - ${note.content}",
        fontSize = 24.sp
    )
}