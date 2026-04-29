package org.example.project.memory.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.aakira.napier.Napier
import org.example.project.memory.viewModel.MemViewModel

@Composable
fun DeckDetailScr(deckId: String, navigateBack: () -> Unit, viewModel: MemViewModel) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val decksDB by viewModel.decksDB.collectAsStateWithLifecycle()
        val actualDeck = decksDB.find { deck -> deck.id == deckId }
        var canDownloadDeck by rememberSaveable{ mutableStateOf(true) }

        Napier.d(tag = "MEMORY_LOG") { "deck ID: $deckId" }
        Text("${actualDeck?.name}", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        Button(onClick = {
            canDownloadDeck = viewModel.downloadCardsFromDeck(actualDeck)
        }) { Text("Download Deck") }
        if (canDownloadDeck){
            Text("Downloaded Successfully!", style = MaterialTheme.typography.headlineMedium, color = Color.Green)
        }else{
            Text("You already downloaded this deck!", style = MaterialTheme.typography.headlineMedium, color = Color.Red)
        }
        Spacer(Modifier.height(24.dp))
        Button(onClick = {
            viewModel.modifySelectedDeck(actualDeck)
            navigateBack()
        }) { Text("Select Deck") }
        Spacer(Modifier.height(24.dp))
        Button(onClick = navigateBack) { Text("Go back") }
    }
}
