package org.example.project.memory

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import org.example.project.memory.navigation.NavigationWrapper
import org.example.project.memory.viewModel.MemViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel = viewModel { MemViewModel() }
        NavigationWrapper(viewModel)
    }
}