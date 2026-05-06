package org.example.project.memory.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val navConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Route.MainMenuRoute::class, Route.MainMenuRoute.serializer())
            subclass(Route.GameRoute::class, Route.GameRoute.serializer())
            subclass(Route.DeckGameSelectorRoute::class, Route.DeckGameSelectorRoute.serializer())
            subclass(Route.DeckInfoSelectorRoute::class, Route.DeckInfoSelectorRoute.serializer())
            subclass(Route.DeckDetailRoute::class, Route.DeckDetailRoute.serializer())
            subclass(Route.SettingsRoute::class, Route.SettingsRoute.serializer())
        }
    }
}
