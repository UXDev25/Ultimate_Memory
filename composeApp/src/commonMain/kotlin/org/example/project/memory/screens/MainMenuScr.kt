package org.example.project.memory.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.project.memory.viewModel.MemViewModel
import org.jetbrains.compose.resources.painterResource
import ultimate_memory.composeapp.generated.resources.Res
import ultimate_memory.composeapp.generated.resources.momazosdiego

@Composable
fun MainMenuScr(navigateTo1: () -> Unit, navigateTo2: () -> Unit, navigateTo3: () -> Unit){
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Image(painterResource(Res.drawable.momazosdiego), contentDescription = "momazos diego ahhgagaagagahh")
        Spacer(Modifier.height(24.dp))
        Text("Main Menu", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        Button(onClick = navigateTo1) { Text("Select Deck") }
        Spacer(Modifier.height(24.dp))
        Button(onClick = navigateTo2) { Text("Play") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = navigateTo3) { Text("Settings") }
    }
}