package com.soloscape.util

import com.soloscape.util.Constants.NOTE_SCREEN_ARG_KEY


sealed class ScreensRoutes(val route: String) {
     object Authentication : ScreensRoutes(route = "auth_screen")
     object Home : ScreensRoutes(route = "home_screen")
     object Note : ScreensRoutes(route = "note_screen?$NOTE_SCREEN_ARG_KEY={$NOTE_SCREEN_ARG_KEY}") {
        fun passNoteId(noteId: String) = "note_screen?$NOTE_SCREEN_ARG_KEY=$noteId"
    }
}
