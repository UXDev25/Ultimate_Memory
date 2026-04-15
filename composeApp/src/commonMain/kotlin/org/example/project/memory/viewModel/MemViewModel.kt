package org.example.project.memory.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MemViewModel: ViewModel() {
    var showMessage by mutableStateOf(false)
        private set

    fun modifyShowMessage(){
        showMessage = !showMessage
    }
}
