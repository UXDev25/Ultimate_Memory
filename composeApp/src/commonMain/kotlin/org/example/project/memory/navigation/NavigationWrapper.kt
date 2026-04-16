package org.example.project.memory.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.NonCancellable.key
import org.example.project.memory.screens.DeckDetailScr
import org.example.project.memory.screens.DeckSelectorScr
import org.example.project.memory.screens.GameScr
import org.example.project.memory.screens.MainMenuScr
import org.example.project.memory.screens.SettingsScr
import org.example.project.memory.viewModel.MemViewModel

@Composable
fun NavigationWrapper(viewModel: MemViewModel){
    val backStack = rememberNavBackStack(navConfig, Route.MainMenuRoute)
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Route.MainMenuRoute> {
                MainMenuScr(
                    navigateTo1 = { backStack.add(Route.DeckSelectorRoute) },
                    navigateTo2 = { backStack.add(Route.GameRoute) },
                    navigateTo3 = { backStack.add(Route.SettingsRoute) },
                )
            }
            entry<Route.DeckSelectorRoute> {
                DeckSelectorScr(
                    navigateBack = { backStack.removeLastOrNull() },
                    navigateToDetail = { backStack.add(Route.DeckDetailRoute(deckId = "")) },
                    viewModel = viewModel
                )
            }
            entry<Route.DeckDetailRoute> { key ->
                DeckDetailScr(deckId = key.deckId,
                    navigateBack = { backStack.removeLastOrNull() },
                    viewModel = viewModel
                )
            }
            entry<Route.GameRoute> {
                GameScr(navigateBack = { backStack.removeLastOrNull() })
            }
            entry<Route.SettingsRoute> {
                SettingsScr(
                    navigateBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}
