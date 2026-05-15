package org.example.project.memory.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.project.memory.viewModel.MemViewModel
import org.jetbrains.compose.resources.painterResource
import ultimate_memory.composeapp.generated.resources.Res
import ultimate_memory.composeapp.generated.resources.momazosdiego

@Composable
fun MainMenuScr(navigateTo1: () -> Unit, navigateTo2: () -> Unit) {
    val vm: MemViewModel = viewModel { MemViewModel() }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF121212)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // LOGO
            Card(
                shape = CircleShape,
                border = BorderStroke(2.dp, Color(0xFFFFD700)),
                elevation = CardDefaults.cardElevation(12.dp),
                modifier = Modifier.size(180.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.momazosdiego),
                    contentDescription = "Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(32.dp))

            // TITLE
            Text(
                text = "ULTIMATE MEMORY",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Challenge your mind",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(Modifier.height(48.dp))

            // MENU BUTTONS
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuButton(
                    text = "PLAY GAME",
                    containerColor = Color(0xFFFFD700),
                    contentColor = Color.Black,
                    modifier = Modifier.testTag("playButton_id")
                ) {
                    vm.setStartGameValues()
                    navigateTo2()
                }

                MenuButton(
                    text = "VIEW DECKS",
                    containerColor = Color(0xFF1E1E1E),
                    contentColor = Color.White
                ) {
                    navigateTo1()
                }

            }
        }
    }
}

@Composable
fun MenuButton(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(0.8f)
            .height(56.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}