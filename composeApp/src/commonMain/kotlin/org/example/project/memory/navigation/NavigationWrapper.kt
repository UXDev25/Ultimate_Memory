package org.example.project.memory.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import org.example.project.memory.screens.DeckDetailScr
import org.example.project.memory.screens.DeckGameSelectorScr
import org.example.project.memory.screens.DeckInfoSelectorScr
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
                    navigateTo1 = { backStack.add(Route.DeckInfoSelectorRoute) },
                    navigateTo2 = { backStack.add(Route.DeckGameSelectorRoute) },
                    navigateTo3 = { backStack.add(Route.SettingsRoute) },
                )
            }
            entry<Route.DeckInfoSelectorRoute> {
                DeckInfoSelectorScr(
                    navigateBack = { backStack.removeLastOrNull() },
                    navigateToDetail = { backStack.add(Route.DeckDetailRoute(deckId = it)) },
                    viewModel = viewModel
                )
            }
            entry<Route.DeckGameSelectorRoute> {
                DeckGameSelectorScr(
                    navigateBack = { backStack.removeLastOrNull() },
                    navigateToGame = { backStack.add(Route.GameRoute) },
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
                GameScr(navigateBack = { val index = backStack.indexOfFirst { it is Route.MainMenuRoute }
                    if (index != -1) {
                        while (backStack.size > index + 1) {
                            backStack.removeLastOrNull()
                        }
                    } else {
                        backStack.removeLastOrNull()
                    } })
            }
            entry<Route.SettingsRoute> {
                SettingsScr(
                    navigateBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}
