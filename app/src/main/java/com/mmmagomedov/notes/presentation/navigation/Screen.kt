package com.mmmagomedov.notes.presentation.navigation

sealed class Screen(val route: String) {
    object NotesList : Screen(route = "list_notes")
    object NoteAdd : Screen(route = "add_note")
    object NoteEdit : Screen(route = "edit_note")
}