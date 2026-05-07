package org.example.project.memory.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.project.memory.viewModel.MemViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import org.example.project.memory.database.Deck


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckGameSelectorScr(
    navigateToGame: () -> Unit,
    navigateBack: () -> Unit,
    viewModel: MemViewModel
){
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(54.dp))
        Text("Select Deck", style = MaterialTheme.typography.headlineMedium)
        Button(onClick = navigateBack) { Text("Go Back") }
        Column(modifier = Modifier.fillMaxSize(1f),
            verticalArrangement = Arrangement.SpaceBetween) {
            //Search bar
            SearchBar(
                query = viewModel.searchedText,
                onQueryChange = { viewModel.onSearchTextChange(it) },
                onSearch = { viewModel.onSearch(it) },
                active = viewModel.active,
                onActiveChange = { viewModel.onActiveChange(it) },
                placeholder = {
                    Text(
                        "Search for a marker...",
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (viewModel.active) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            modifier = Modifier.clickable {

                                if (viewModel.searchedText.isNotEmpty()) {
                                    viewModel.onSearchTextChange("")
                                } else {
                                    viewModel.onActiveChange(false)
                                }
                            }
                        )
                    }
                }
            ) {
                // --- ZONA DE CONTINGUT DE LA BARRA ---

                // CAS A: L'usuari està escrivint -> Ensenyem RESULTATS (Cards)
                if (viewModel.searchedText.isNotEmpty()) {
                    DeckGameList(viewModel, navigateToGame)
                }
                // CAS B: La barra està buida -> Ensenyem HISTORIAL
                else {
                    if (viewModel.searchHistory.isNotEmpty()) {
                        Text(
                            text = "Cerques recents",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Column(verticalArrangement = Arrangement.SpaceBetween)
                        {
                            LazyColumn {
                                items(viewModel.searchHistory) { itemHistorial ->
                                    ListItem(
                                        headlineContent = { Text(itemHistorial) },
                                        leadingContent = { Icon(Icons.Default.Refresh, contentDescription = null) },
                                        modifier = Modifier.clickable {
                                            viewModel.onSearchTextChange(itemHistorial)
                                        }
                                    )
                                }

                                // Botó per esborrar historial al final de la llista
                                item {
                                    TextButton(onClick = { viewModel.onClearHistory() }) {
                                        Text("Erase history")
                                    }
                                }
                            }
                            DeckGameList(viewModel, navigateToGame)
                        }
                    } else {
                        DeckGameList(viewModel, navigateToGame)
                    }
                }
            }
            DeckGameList(viewModel, navigateToGame)
        }
    }
}
@Composable
fun DeckGameList(viewModel: MemViewModel, navigateToGame: () -> Unit){
    val decksDB by viewModel.decksDB.collectAsStateWithLifecycle()
    LazyColumn(modifier = Modifier
        .fillMaxSize(1f)
        .background(MaterialTheme.colorScheme.background)){
        if (viewModel.searchedText.isNotEmpty())
        {
            items(viewModel.filteredNames, key = { deck -> deck.id }) { deck ->
                DeckGameItem(deck,navigateToGame, viewModel)
            }
        }
        else
        {
            items(items = decksDB, key = { deck -> deck.id }) { deck ->
                DeckGameItem(deck, navigateToGame, viewModel)
            }
        }
    }
}

@Composable
fun DeckGameItem(actualDeck: Deck, navigateToGame: () -> Unit, viewModel: MemViewModel) {
    val scope = rememberCoroutineScope()
    var isDownloading by rememberSaveable { mutableStateOf(false) }
    Napier.d(tag = "MEMORY_LOG") { "Deck item func reached" }
    Card(border = BorderStroke(Dp.Hairline, color = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .testTag("deckItem_id")
            .fillMaxWidth()
            .padding(5.dp, 5.dp)
            .clickable(enabled = !isDownloading){
                scope.launch {
                    isDownloading = true
                    val success = viewModel.selectAndDownloadDeck(actualDeck)
                    isDownloading = false
                    if (success) {
                        viewModel.CreateInGameDeck()
                        navigateToGame()
                    } else {
                        Napier.e(tag = "MEMORY_LOG") { "[GameScr] Error descarregant el deck" }
                    }
                }
                      },
        shape = MaterialTheme.shapes.small
    )    {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = actualDeck.imageUrl,
                contentDescription = "Icon of ${actualDeck.name}",
                modifier = Modifier
                    .size(64.dp))
            Column {
                Text(text = actualDeck.description,
                    color = MaterialTheme.colorScheme.primary,
                    softWrap = false,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis)
            }
        }
    }
}