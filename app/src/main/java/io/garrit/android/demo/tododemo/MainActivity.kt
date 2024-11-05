package io.garrit.android.demo.tododemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.garrit.android.demo.tododemo.ui.theme.TodoDemoTheme
import java.util.UUID

data class Note(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var text: String = "",
    var isChecked: MutableState<Boolean> = mutableStateOf(false)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val notes = remember { mutableStateListOf<Note>() }
            var selectedNote by remember { mutableStateOf<Note?>(null) }
            var screen by remember { mutableStateOf("MainScreen") }

            TodoDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (screen) {
                        "MainScreen" -> MainScreen(
                            list = notes,
                            onNoteClick = {
                                selectedNote = it
                                screen = "DetailScreen"
                            },
                            onAddNote = {
                                selectedNote = null
                                screen = "EditScreen"
                            }
                        )
                        "DetailScreen" -> selectedNote?.let { note ->
                            NoteDetailScreen(
                                note = note,
                                onEdit = { screen = "EditScreen" },
                                onDelete = {
                                    notes.remove(note)
                                    screen = "MainScreen"
                                }
                            )
                        }
                        "EditScreen" -> EditNoteScreen(
                            note = selectedNote,
                            onSave = { note ->
                                if (selectedNote == null) {
                                    notes.add(note)
                                } else {
                                    notes[notes.indexOf(selectedNote)] = note
                                }
                                screen = "MainScreen"
                            },
                            onCancel = { screen = "MainScreen" }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(list: MutableList<Note>, onNoteClick: (Note) -> Unit, onAddNote: () -> Unit) {
    val checkedNotes = list.filter { it.isChecked.value }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Notes",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Show "Delete Selected" button if any notes are checked
        if (checkedNotes.isNotEmpty()) {
            Button(
                onClick = {
                    list.removeAll(checkedNotes) // Remove all checked notes from the list
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete Selected (${checkedNotes.size})")
            }
        }

        Button(
            onClick = onAddNote,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Add Note")
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(list) { note ->
                ListItem(note = note, onClick = { onNoteClick(note) })
            }
        }
    }
}

@Composable
fun ListItem(note: Note, onClick: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = note.isChecked.value,
                onCheckedChange = { note.isChecked.value = !note.isChecked.value },
                colors = CheckboxDefaults.colors(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = note.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun NoteDetailScreen(note: Note, onEdit: () -> Unit, onDelete: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Title: ${note.title}",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Content: ${note.text}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onEdit, modifier = Modifier.fillMaxWidth()) {
            Text("Edit")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)) {
            Text("Delete")
        }
    }
}
@Composable
fun RowView(task: Note) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isChecked.value,
            onCheckedChange = {
                task.isChecked.value = !task.isChecked.value
            }
        )
        Text(task.title)
    }
}

@Preview(showBackground = true)
@Composable
fun RowViewPreview() {
    TodoDemoTheme {
        RowView(Note(title = "Hello"))
    }
}