package com.red9xela.notesmd.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.red9xela.notesmd.presentation.navigation.NavGraph
import com.red9xela.notesmd.presentation.ui.theme.NotesMdTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesMdTheme {
                NavGraph()
            }
        }
    }
}


