package org.example.project.memory.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import org.example.project.memory.database.Card
import org.example.project.memory.viewModel.MemViewModel
import kotlin.collections.emptyList
import kotlin.collections.mutableListOf

@Composable
fun GameScr(navigateBack: () -> Unit){
    val vm: MemViewModel = viewModel { MemViewModel() }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize(1f)){
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val cardsDB by vm.cardsDB.collectAsStateWithLifecycle()
            val cardsList = remember(cardsDB) {
                if (cardsDB.isNotEmpty()) {
                    val elementsNum: Int
                    if (cardsDB.size > 16){ elementsNum = 16 }else{ elementsNum = cardsDB.size}
                    val limitedList = cardsDB.take(elementsNum)
                    val shuffledList = (limitedList + limitedList).shuffled()
                    shuffledList
                } else {
                    emptyList()
                }
            }

            LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier
                .background(MaterialTheme.colorScheme.background)){
                items(items = cardsList) {
                    CardItem(item = it, vm)
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(onClick = navigateBack) { Text("Surrender") }
        }
    }
}

@Composable
fun CardItem(item: Card, viewModel: MemViewModel){
    Card(border = BorderStroke(Dp.Hairline, color = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp, 5.dp)
            .clickable(onClick = {
                viewModel.changeCardOpen(item.id)
            }),
        shape = MaterialTheme.shapes.small
    )   {
    }
        var url: String?
        if (viewModel.isCardOpen && viewModel.openCardId == item.id){
            url = item.imageUrl
        } else{
            url = viewModel.decksDB.value.find { deck -> deck.id == item.deckId }?.imageUrl
        }
        AsyncImage(
            model = url,
            contentDescription = "Icon of ${item.name}",
            modifier = Modifier
                .size(64.dp))
    }