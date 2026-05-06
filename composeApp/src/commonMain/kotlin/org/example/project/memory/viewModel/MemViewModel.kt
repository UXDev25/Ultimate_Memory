package org.example.project.memory.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.example.project.memory.database.Card
import org.example.project.memory.database.Deck
import org.example.project.memory.database.DecksRepository
import org.example.project.memory.screens.CardItem

class MemViewModel: ViewModel() {

    //------------------DATABASE------------------
    private val repository = DecksRepository()
    private val _decks = MutableStateFlow<List<Deck>>(emptyList())
    val decksDB: StateFlow<List<Deck>> = _decks
    private val _cards = MutableStateFlow<List<Card>>(emptyList())
    val cardsDB: StateFlow<List<Card>> = _cards

    private val _downloadedCards = MutableStateFlow<List<Card>>(emptyList())

    //------------------CARDS AND IN-GAME VARIABLES------------------

    var selectedDeck by mutableStateOf<Deck?>(null)
        private set

    var defCardList = mutableStateListOf<CardItem>()
        private set

    var remainingCardsNum by mutableStateOf(0)
        private set

    fun setRemainingCards(cardsLeft: Int){
        remainingCardsNum = cardsLeft
    }

    var indexList = mutableStateListOf<Int>()
        private set

    var isClickable by mutableStateOf(true)
        private set

    var isGameWon by mutableStateOf(false)
        private set

    fun changeGameWon(isWon: Boolean){
        isGameWon = isWon
    }

    var isGameLost by mutableStateOf(false)
        private set

    fun modifyCardList(mutableList: MutableList<CardItem>){
        defCardList = mutableList.toMutableStateList()
        Napier.d(tag ="MEMORY_LOG"){"[modifyCardList] defCardList size: ${defCardList.size}"}
    }

    //REMOVE THIS BELOW
    fun modifySelectedDeck(deck: Deck?) : Boolean{ // set an error if deck is not downloaded
        selectedDeck = deck
        _cards.value = emptyList()
        _cards.value = _downloadedCards.value.filter { card -> card.deckId == deck?.id }
        if (_cards.value.count() == 0) return false

        return true
    }

    fun downloadCardsFromDeck(deck: Deck?) : Boolean{
        for (card in _downloadedCards.value){
            if (card.deckId == deck?.id) return false
        }
        //loadCards(deck?.id ?: "no deck found")
        if (!modifySelectedDeck(deck)) return false

        return true
    }
    //REMOVE THIS ABOVE

    suspend fun selectAndDownloadDeck(deck: Deck?): Boolean {
        val deckId = deck?.id ?: return false

        val alreadyDownloaded = _downloadedCards.value.filter { it.deckId == deckId }
        if (alreadyDownloaded.isNotEmpty()) {
            _cards.value = alreadyDownloaded
            return true
        }
        return loadCards(deck.id)
    }

    fun onCardClicked(index: Int){
        if (defCardList[index].isFlipped || !isClickable) return
        flipCard(index)
        indexList.add(defCardList[index].id)
        if (indexList.size >= 2){
            Napier.d(tag = "MEMORY_LOG") { "[OnCardClicked] id before entering CheckCards, id 1: ${defCardList[0].card.id}, id2: ${defCardList[1].card.id}" }
            checkCards(indexList[0], indexList[1])
        }
    }
    fun checkCards(cardIndex1: Int, cardIndex2: Int) {
        isClickable = false
        Napier.d(tag = "MEMORY_LOG") { "Checking cards..." }
        val usedCardsId = Pair(defCardList[cardIndex1].card.id, defCardList[cardIndex2].card.id)
        viewModelScope.launch {
            delay(1000)
            Napier.d(tag = "MEMORY_LOG") { "1000 miliseconds completed" }
            if (usedCardsId.first == usedCardsId.second){
                if (checkIsGameWon()) return@launch
                resetSelectAction()
                Napier.d(tag = "MEMORY_LOG") { "id are equal, id1: ${usedCardsId.first}, indexPair2: ${usedCardsId.second}" }
                return@launch
            }
            flipCard(cardIndex1)
            flipCard(cardIndex2)
            resetSelectAction()
        }
    }

    fun checkIsGameWon() : Boolean{
        remainingCardsNum -= 2
        if (remainingCardsNum <= 0){
            isGameWon = true
            resetSelectAction()
            return true
        }
        return false
    }
    private fun resetSelectAction(){
        indexList.clear()
        isClickable = true
    }
    private fun flipCard(index: Int){
        val actualCard = defCardList[index]
        defCardList[index] = actualCard.copy(isFlipped = !defCardList[index].isFlipped)
    }

    fun resetGame(){
        viewModelScope.launch {
            delay(1000)
            defCardList.clear()
        }
        Napier.d(tag = "MEMORY_LOG") { "[resetGame] defCardList size: ${defCardList.size}"}
    }

    fun setStartGameValues(){
        isGameWon = false
        isGameLost = false
        resetSelectAction()
    }

    //-------------LOADERS--------------
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
    private suspend fun loadCards(deckId: String): Boolean {
        return try {
            val newCards = repository.getAllCardsFromDeck(deckId)
            _downloadedCards.value += newCards
            _cards.value = newCards
            newCards.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }


    //-----------------TIMER---------------------------

    var timer = 5000f // 50 SECONDS (this are the seconds you want * 100)
        private set
    private var timeLeftVm = 0f
    fun calculateTimePercentage(actualTime: Float): Float {
        return actualTime / timer
    }
    fun timerFlow() = flow {
        var current = timer
        var currentAux = current
        while (current >= 0) {
            emit(current)
            delay(10) // wait 0.01 second
            current--
            if (isGameWon){
                currentAux = current
                current = 0f
                emit(currentAux)
            }
        }
        Napier.d(tag = "MEMORY_LOG"){"[memViewModel] reached timerFlow End"}
        if (!isGameWon) isGameLost = true;
        Napier.d(tag = "MEMORY_LOG"){"[memViewModel] isGameLost Value = $isGameLost"}
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