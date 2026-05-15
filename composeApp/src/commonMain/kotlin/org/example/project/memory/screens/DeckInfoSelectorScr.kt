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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil3.compose.AsyncImage
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import org.example.project.memory.database.Deck

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckInfoSelectorScr(
    navigateToDetail: (String) -> Unit,
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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "DECK LIBRARY",
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
                    placeholder = { Text("Search in library...", color = Color.Gray) },
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
                    DeckList(viewModel, navigateToDetail)
                }
            }
            if (!viewModel.active) {
                Spacer(Modifier.height(16.dp))
                DeckList(viewModel, navigateToDetail)
            }
        }
    }
}
@Composable
fun DeckList(viewModel: MemViewModel, navigateToDetail: (String) -> Unit) {
    val decksDB by viewModel.decksDB.collectAsStateWithLifecycle()
    val listToShow = if (viewModel.searchedText.isNotEmpty()) viewModel.filteredNames else decksDB

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = listToShow, key = { it.id }) { deck ->
            DeckItem(deck, navigateToDetail, viewModel)
        }
    }
}

@Composable
fun DeckItem(actualDeck: Deck, navigateToDetail: (String) -> Unit, viewModel: MemViewModel) {
    val scope = rememberCoroutineScope()
    var isDownloading by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable(enabled = !isDownloading) {
                scope.launch {
                    isDownloading = true
                    if (viewModel.selectAndDownloadDeck(actualDeck)) {
                        navigateToDetail(actualDeck.id)
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
            AsyncImage(
                model = actualDeck.imageUrl?.toProxyUrl(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier
                    .size(90.dp)
                    .clip(MaterialTheme.shapes.medium)
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = actualDeck.name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = actualDeck.description,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Icona d'informació lateral
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Details",
                tint = Color(0xFFFFD700), // El nostre daurat corporatiu
                modifier = Modifier.padding(end = 16.dp).size(20.dp)
            )
        }
    }
}