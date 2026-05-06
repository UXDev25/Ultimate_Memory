package org.example.project.memory.screens

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import io.github.aakira.napier.Napier
import org.example.project.memory.viewModel.MemViewModel
import kotlin.collections.emptyList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScr(navigateBack: () -> Unit) {
    val vm: MemViewModel = viewModel { MemViewModel() }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize(1f)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            //GETTING AND SHUFFLING CARDS
            val cardsDB by vm.cardsDB.collectAsStateWithLifecycle()
            val cardsList = remember(cardsDB) {
                if (cardsDB.isNotEmpty()) {
                    val elementsNum: Int = if (cardsDB.size > 16) {
                        16
                    } else {
                        cardsDB.size
                    }
                    val limitedList = cardsDB.take(elementsNum)
                    val shuffledList = (limitedList + limitedList).shuffled()
                    shuffledList
                } else {
                    emptyList()
                }
            }
            Napier.d(tag = "MEMORY_LOG"){"[GameScr] size de cardsList: ${cardsList.size}"}
            vm.setRemainingCards(cardsList.size)
            val finalCardList: MutableList<CardItem> = arrayListOf()

            for ((i, cardItem) in cardsList.withIndex()) {
                //Napier.d(tag = "MEMORY_LOG") { "[GameScr] id of each card before starting game, id: ${cardItem.id}" }
                finalCardList.add(CardItem(i, cardItem, false))
            }
            vm.modifyCardList(finalCardList)


            //TIMER

            Timer(vm)
            Spacer(Modifier.height(24.dp))

            //CARDS VIEW
            Box(contentAlignment = Alignment.Center) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4), modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    items(items = vm.defCardList) {
                        CardItem(item = it, vm)
                    }
                }
                if (vm.isGameWon) {
                    Text(
                        "YOU WON!",
                        fontSize = 55.sp, color = Color.Green
                    )
                }
                if (vm.isGameLost) {
                    Napier.d(tag = "MEMORY_LOG") { "[GameScr] reached isGameLost" }
                    Text(
                        "YOU LOST...",
                        fontSize = 55.sp, color = Color.Red
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(onClick = {
                vm.resetGame()
                navigateBack()
            }) { Text("Surrender") }
        }
    }
}

@Composable
fun CardItem(item: CardItem, viewModel: MemViewModel){
    Card(border = BorderStroke(Dp.Hairline, color = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp, 5.dp)
            .clickable(onClick = {
                if (viewModel.isClickable && !viewModel.isGameLost){
                    Napier.d(tag = "MEMORY_LOG") { "[GameScr] card clicked id: ${item.card.id}, index: ${item.id}" }
                    viewModel.onCardClicked(item.id)
                }
            }),
        shape = MaterialTheme.shapes.small
    )   {
        val url: String? = if (item.isFlipped){
            item.card.imageUrl
        } else {
            viewModel.decksDB.value.find { deck -> deck.id == item.card.deckId }?.imageUrl
        }
        AsyncImage(
            model = url,
            contentDescription = "",
            modifier = Modifier
                .size(64.dp))
    }
}

@Composable
fun Timer(viewModel: MemViewModel){
    val timeLeft by remember { viewModel.timerFlow() }.collectAsState(initial = viewModel.timer)
    LinearProgressIndicator(
        progress = {viewModel.calculateTimePercentage(timeLeft)},
        color = Color.Black,
        trackColor = Color.Gray,
        strokeCap = StrokeCap.Butt,
        modifier = Modifier
            .height(15.dp)
    )

    Text(
        text = "Temps: $timeLeft"
    )
}

