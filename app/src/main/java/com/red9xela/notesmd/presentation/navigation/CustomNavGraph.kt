package com.red9xela.notesmd.presentation.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import com.red9xela.notesmd.presentation.screens.editing.EditScreen
import com.red9xela.notesmd.presentation.screens.folders.FoldersScreen
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
        composable(Screen.EditNote.route) {
            EditScreen(

            )
        }
        composable(Screen.Folders.route) {
            FoldersScreen(

            )
        }

    }

}

sealed class Screen(val route: String) {
    data object Notes : Screen(route = "notes")
    data object Folders : Screen(route = "folders")

    data object EditNote : Screen(route = "edit_note/{note_id}") {
        fun createRoute(noteId: Int): String {
            return "edit_note/$noteId"
        }

        fun getNoteId(arguments: Bundle?): Int {
            return arguments?.getString("note_id")?.toInt() ?: 0
        }
    }
}
