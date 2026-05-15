package org.example.project.memory.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class Route: NavKey {
    @Serializable
    data object MainMenuRoute : Route()
    @Serializable
    data object GameRoute : Route()
    @Serializable
    data object DeckInfoSelectorRoute : Route()
    @Serializable
    data object DeckGameSelectorRoute : Route()
    @Serializable
    data class DeckDetailRoute(val deckId: String) : Route()
}
