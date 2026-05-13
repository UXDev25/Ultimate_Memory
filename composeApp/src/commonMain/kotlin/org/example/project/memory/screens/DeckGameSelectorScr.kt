package org.example.project.memory.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF121212)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = navigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "SELECT DECK",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // SEARCH BAR ESTILITZADA
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                SearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    query = viewModel.searchedText,
                    onQueryChange = { viewModel.onSearchTextChange(it) },
                    onSearch = { viewModel.onSearch(it) },
                    active = viewModel.active,
                    onActiveChange = { viewModel.onActiveChange(it) },
                    colors = SearchBarDefaults.colors(
                        containerColor = Color(0xFF1E1E1E),
                        inputFieldColors = TextFieldDefaults.colors(focusedTextColor = Color.White)
                    ),
                    placeholder = { Text("Search decks...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    trailingIcon = {
                        if (viewModel.active && viewModel.searchedText.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color.White,
                                modifier = Modifier.clickable { viewModel.onSearchTextChange("") }
                            )
                        }
                    }
                ) {
                    if (viewModel.searchedText.isEmpty() && viewModel.searchHistory.isNotEmpty()) {
                        HistorySection(viewModel)
                    }
                    DeckGameList(viewModel, navigateToGame)
                }
            }

            if (!viewModel.active) {
                Spacer(Modifier.height(16.dp))
                DeckGameList(viewModel, navigateToGame)
            }
        }
    }
}

@Composable
fun HistorySection(x0: MemViewModel) {
    TODO("Not yet implemented")
}

@Composable
fun DeckGameList(viewModel: MemViewModel, navigateToGame: () -> Unit) {
    val decksDB by viewModel.decksDB.collectAsStateWithLifecycle()
    val listToShow = if (viewModel.searchedText.isNotEmpty()) viewModel.filteredNames else decksDB

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = listToShow, key = { it.id }) { deck ->
            DeckGameItem(deck, navigateToGame, viewModel)
        }
    }
}

@Composable
fun DeckGameItem(actualDeck: Deck, navigateToGame: () -> Unit, viewModel: MemViewModel) {
    val scope = rememberCoroutineScope()
    var isDownloading by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .testTag("deckItem_id")
            .fillMaxWidth()
            .height(100.dp)
            .clickable(enabled = !isDownloading) {
                scope.launch {
                    isDownloading = true
                    if (viewModel.selectAndDownloadDeck(actualDeck)) {
                        viewModel.CreateInGameDeck()
                        navigateToGame()
                    }
                    isDownloading = false
                }
            },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(100.dp)) {
                AsyncImage(
                    model = actualDeck.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                if (isDownloading) {
                    Box(
                        Modifier.fillMaxSize().background(Color.Black.copy(0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFFFD700), modifier = Modifier.size(24.dp))
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = actualDeck.name.uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = actualDeck.description,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}