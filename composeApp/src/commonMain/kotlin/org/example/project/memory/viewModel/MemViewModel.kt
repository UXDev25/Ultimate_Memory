package org.example.project.memory.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.project.memory.database.Card
import org.example.project.memory.database.Deck
import org.example.project.memory.database.DecksRepository

class MemViewModel: ViewModel() {

    //------------------DATABASE------------------
    private val repository = DecksRepository()
    private val _decks = MutableStateFlow<List<Deck>>(emptyList())
    val decksDB: StateFlow<List<Deck>> = _decks
    private val _cards = MutableStateFlow<List<Card>>(emptyList())
    val cardsDB: StateFlow<List<Card>> = _cards

    //------------------MISC------------------
    var selectedDeck by mutableStateOf<Deck?>(null)
        private set

    fun modifySelectedDeck(deck: Deck?){
        selectedDeck = deck
        loadCards(deck?.id ?: "no deck found")
    }

    var isCardOpen by mutableStateOf(false)
        private set
    var openCardId by mutableStateOf("")
        private set

    fun changeCardOpen(cardState : Boolean, cardId: String){
        isCardOpen = cardState
    }

    fun changeCardOpen(cardId: String){
        isCardOpen = !isCardOpen
        openCardId = cardId
    }

    init{
        Napier.d(tag = "MEMORY_LOG") { "Viewmodel created" }
        Napier.d(tag = "MEMORY_LOG") { "all downloaded cards: ${_cards.value.size} cards" }
        loadDecks()
    }

    private fun loadDecks() {
        Napier.d(tag = "MEMORY_LOG") { "Launching coroutine" }
        viewModelScope.launch {
            try {
                _decks.value = repository.getAllDecks()
                allDecks = _decks.value
                Napier.d(tag = "MEMORY_LOG") { "decks downloaded: ${_decks.value.size}" }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun loadCards(deckId: String) {
        viewModelScope.launch {
            try {
                _cards.value = repository.getAllCardsFromDeck(deckId)
                Napier.d(tag = "MEMORY_LOG") { "downloaded: ${_cards.value.size} cards" }
            } catch (e: Exception) {
                e.printStackTrace()
                Napier.d(tag = "MEMORY_LOG") { "failed card downloading: ${e.message}" }
            }
        }
    }


    //------------------SEARCH BAR------------------------

    // 1. ESTAT DEL TEXT (Query)
    var searchedText by mutableStateOf("")
        private set

    // 2. ESTAT DE SI LA BARRA ESTÀ ACTIVA
    var active by mutableStateOf(false)

    // 3. ESTAT DE L'HISTORIAL (El que ja teníem)
    var searchHistory = mutableStateListOf<String>()
        private set

    // 4. NOVES DADES:
    private var allDecks: List<Deck> = emptyList()

    // 5. RESULTATS FILTRATS: La llista que ensenyarem quan busquem
    var filteredNames = mutableStateListOf<Deck>()
        private set

    // FUNCIONS (Events)

    fun onSearchTextChange(text: String) {
        searchedText = text

        // LÒGICA DE CERCA:
        if (text.isEmpty()) {
            // Si no hi ha text, buidem la llista de resultats
            filteredNames.clear()
        } else {
            // Si hi ha text, filtrem la llista completa
            filteredNames.clear()
            val results = allDecks.filter { deck ->
                // 'ignoreCase = true' fa que li sigui igual majúscules que minúscules
                deck.name.contains(text, ignoreCase = true)
            }
            filteredNames.addAll(results)
        }
    }

    fun onActiveChange(isActive: Boolean) {
        active = isActive
    }

    fun onSearch(text: String) {
        if (text.isNotEmpty()) {
            searchHistory.add(text)
            // No tanquem la barra ni esborrem el text immediatament
            // perquè l'usuari vulgui veure el resultat
            onActiveChange(false)
        }
    }

    fun onClearHistory() {
        searchHistory.clear()
    }
}