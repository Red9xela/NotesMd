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
        composable (Screen.Notes.route) {
            EditScreen(

            )
        }
        composable (Screen.Notes.route) {
            FoldersScreen(

            )
        }

    }

}

sealed class Screen(val route: String) {
    data object Notes : Screen(route = "notes")
    data object Folders : Screen("Folders")


    data object EditNote : Screen("edit_note/{note_id}") {
        fun createRoute(noteId: Int): String { //edit_note/5
            return "edit_note/$noteId"
        }

        fun getNoteId(arguments: Bundle?): Int {
            return arguments?.getString("note_id")?.toInt() ?: 0
        }
    }
}

sealed interface CustomScreen {
    data object Notes : CustomScreen

    data object Folders : CustomScreen

    data class EditNote(val noteId: Int) : CustomScreen
}