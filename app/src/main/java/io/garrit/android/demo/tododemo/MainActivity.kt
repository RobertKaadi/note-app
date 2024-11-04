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
fun MainScreen(list: MutableList<Note>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        TextInputView(list = list)
        ListView(list = list)
    }
}

@Composable
fun TextInputView(list: MutableList<Note>) {
    var text by rememberSaveable {
        mutableStateOf("")
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(value = text, onValueChange = {
            text = it
        })
        Button(onClick = { 
            list.add(Note(title = text))
            text = ""
        }) {
            Text("Add")
        }
    }
}

@Composable
fun ListView(list: List<Note>) {
    LazyColumn {
        items(list) { task ->
            RowView(task)
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