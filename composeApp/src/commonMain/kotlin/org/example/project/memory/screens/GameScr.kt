package org.example.project.memory.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import io.github.aakira.napier.Napier
import org.example.project.memory.audio.AudioPlayer
import org.example.project.memory.viewModel.MemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScr(navigateBack: () -> Unit) {
    val vm: MemViewModel = viewModel { MemViewModel() }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF121212)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(Modifier.height(40.dp))

                Timer(vm)

                Spacer(Modifier.height(32.dp))

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    var gridCellsCount by rememberSaveable{mutableStateOf(4)}
                    var scSizeValue by rememberSaveable{mutableStateOf(1f)}
                    BoxWithConstraints {
                        if (maxWidth < 600.dp) {
                            gridCellsCount = 4
                            scSizeValue = 1f
                        } else if (maxWidth < 1000.dp){
                            gridCellsCount = 10
                            scSizeValue = 0.7f
                        }
                        else{
                            gridCellsCount = 7
                            scSizeValue = 0.7f
                        }
                    }
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(gridCellsCount),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth(scSizeValue)
                            ) {
                                items(items = vm.defCardList) {
                                    CardItem(item = it, vm)
                                }
                            }
                        }
                Spacer(Modifier.height(24.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .testTag("surrender_id"),
                    onClick = {
                        vm.resetGame()
                        navigateBack()
                    }
                ) {
                    Text("Surrender", fontSize = 18.sp)
                }
                Spacer(Modifier.height(40.dp))
                    }
                }
            }

            if (vm.isGameWon || vm.isGameLost) {
                EndGameOverlay(isWin = vm.isGameWon)
            }
        }



@Composable
fun CardItem(item: CardItem, viewModel: MemViewModel) {
    val rotation by animateFloatAsState(
        targetValue = if (item.isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "CardRotation"
    )
    val audioPlayer = remember { AudioPlayer() }
    Card(
        border = BorderStroke(2.dp, if (item.isFlipped) Color(0xFFFFD700) else Color(0xFF444444)),
        modifier = Modifier
            .aspectRatio(0.75f)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable(
                enabled = !item.isFlipped && !viewModel.isGameLost && viewModel.isClickable,
                onClick = {
                    viewModel.onCardClicked(item.id)
                    audioPlayer.playSound()
                }
            ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = viewModel.decksDB.value.find { it.id == item.card.deckId }?.imageUrl?.toProxyUrl(),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .clip(MaterialTheme.shapes.small)
                    .graphicsLayer {
                        alpha = if (rotation <= 90f) 1f else 0f
                    }
            )

            AsyncImage(
                model = item.card.imageUrl.toProxyUrl(),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .clip(MaterialTheme.shapes.small)
                    .graphicsLayer {
                        rotationY = 180f
                        alpha = if (rotation > 90f) 1f else 0f
                    }
            )
        }
    }
}
@Composable
fun Timer(viewModel: MemViewModel) {
    val timeLeft by remember { viewModel.timerFlow() }.collectAsState(initial = viewModel.timer.toFloat())
    val progress = viewModel.calculateTimePercentage(timeLeft)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "TIME LEFT: ${timeLeft.toInt()}s",
            color = if (timeLeft < 10) Color.Red else Color.White,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LinearProgressIndicator(
            progress = { progress },
            color = when {
                progress > 0.6f -> Color.Green
                progress > 0.3f -> Color.Yellow
                else -> Color.Red
            },
            trackColor = Color(0xFF333333),
            strokeCap = StrokeCap.Round,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(12.dp)
                .clip(MaterialTheme.shapes.large)
        )
    }
}

@Composable
fun EndGameOverlay(isWin: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        val audioPlayer = remember { AudioPlayer() }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isWin) "VICTORY!" else "GAME OVER",
                fontSize = 60.sp,
                color = if (isWin) Color(0xFFFFD700) else Color.Red,
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = if (isWin) "Well done, Champion!" else "Try again, don't give up!",
                color = Color.White,
                fontSize = 18.sp
            )
            if (isWin) audioPlayer.playVictory()
        }
    }
}

