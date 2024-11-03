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

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    var isChecked: MutableState<Boolean> = mutableStateOf(false)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val list = remember {
                mutableStateListOf(Task(title = "Hello"), Task(title = "World"))
            }

            TodoDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(list = list)
                }
            }
        }
    }
}

@Composable
fun MainScreen(list: MutableList<Task>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        TextInputView(list = list)
        ListView(list = list)
    }
}

@Composable
fun TextInputView(list: MutableList<Task>) {
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
            list.add(Task(title = text))
            text = ""
        }) {
            Text("Add")
        }
    }
}

@Composable
fun ListView(list: List<Task>) {
    LazyColumn {
        items(list) { task ->
            RowView(task)
        }
    }
}

@Composable
fun RowView(task: Task) {
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
        RowView(Task(title = "Hello"))
    }
}