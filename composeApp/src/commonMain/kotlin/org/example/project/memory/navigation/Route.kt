package org.example.project.memory.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class Route: NavKey {
    @Serializable
    data object MainMenuRoute : Route()
    @Serializable
    data object GameRoute : Route()
    @Serializable
    data object DeckSelectorRoute : Route()
    @Serializable
    data object SettingsRoute : Route()
    @Serializable
    data class DeckDetailRoute(val deckId: String) : Route()
}
