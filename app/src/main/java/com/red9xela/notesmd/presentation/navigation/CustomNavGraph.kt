package com.red9xela.notesmd.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import com.red9xela.notesmd.presentation.screens.notes.NotesScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Notes.route
    ) {
        composable(Screen.Notes.route) {
            NotesScreen(
                onNoteClick = {},
                onAddNoteClick = {}
            )
        }
    }

}

sealed class Screen(val route: String) {
    data object Notes : Screen(route = "notes")
}

sealed interface CustomScreen {
    data object Notes : CustomScreen

    data object CreateNote : CustomScreen

    data class EditNote(val noteId: Int) : CustomScreen
}