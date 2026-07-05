package com.red9xela.notesmd.presentation.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
                onNoteClick = { navController.navigate(Screen.EditNote.createRoute(it.id)) },
                onAddNoteClick = {
                    navController.navigate(Screen.CreateNote.route)
                }
            )
        }
        composable(
            route = Screen.EditNote.route,
            arguments = listOf(navArgument("note_id") {
                type = NavType.IntType
            })
        ) {
            val noteId = Screen.EditNote.getNoteId(it.arguments)
            EditScreen(
                noteId = noteId,
                onFinished = {
                    navController.popBackStack()
                })
        }
        composable(Screen.Folders.route) {
            FoldersScreen(

            )
        }
        composable(Screen.CreateNote.route) {
            EditScreen(
                noteId = -1,
                onFinished = {
                    navController.popBackStack()
                })
        }
    }

}

sealed class Screen(val route: String) {
    data object Notes : Screen(route = "notes")
    data object Folders : Screen(route = "folders")
    data object CreateNote : Screen(route = "create_note")
    data object EditNote : Screen(route = "edit_note/{note_id}") {
        fun createRoute(noteId: Int): String {
            return "edit_note/$noteId"
        }

        fun getNoteId(arguments: Bundle?): Int {
            return arguments?.getString("note_id")?.toInt() ?: 0
        }
    }
}
