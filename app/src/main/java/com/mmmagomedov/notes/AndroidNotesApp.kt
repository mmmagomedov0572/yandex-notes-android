package com.mmmagomedov.notes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mmmagomedov.notes.presentation.navigation.Screen
import com.mmmagomedov.notes.presentation.note_edit.NoteEditScreen
import com.mmmagomedov.notes.presentation.notes_list.NotesListScreen

@Composable
fun AndroidNotesApp(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        AppNavGraph(
            modifier = Modifier.padding(innerPadding),
            startDestination = Screen.NotesList.route
        )
    }
}

@Composable
private fun AppNavGraph(
    modifier: Modifier = Modifier,
    startDestination: String,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Screen.NotesList.route) {
            NotesListScreen(
                onNoteCreate = { navController.navigate(Screen.NoteAdd.route) },
                onNoteModify = { uid -> navController.navigate("${Screen.NoteEdit.route}/$uid") }
            )
        }

        composable(route = Screen.NoteAdd.route) {
            NoteEditScreen(
                noteId = null,
                title = "Создание",
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Screen.NoteEdit.route}/{noteUid}",
            arguments = listOf(
                navArgument("noteUid") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val noteUid = backStackEntry.arguments?.getString("noteUid")
            NoteEditScreen(
                noteId = noteUid,
                title = "Редактирование",
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}