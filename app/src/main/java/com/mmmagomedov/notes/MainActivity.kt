package com.mmmagomedov.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.mmmagomedov.notes.data.FileNotebook
import com.mmmagomedov.notes.domain.Note
import com.mmmagomedov.notes.ui.theme.AndroidNotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidNotesTheme {
                val context = LocalContext.current
                val storage = remember { FileNotebook(context) } // избегаем утечки при рекомпозиции с помощью remember

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    storage.add(
                        Note(
                            title = "note 1",
                            content = "content of note 1",
                            color = android.graphics.Color.BLUE
                        )
                    )

                    storage.notes.forEach { note ->
                        Greeting(
                            name = note.title + note.content,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidNotesTheme {
        Greeting("Android")
    }
}