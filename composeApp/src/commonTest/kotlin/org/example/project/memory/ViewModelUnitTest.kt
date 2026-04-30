import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.example.project.memory.viewModel.MemViewModel
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelUnitTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: MemViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MemViewModel()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState() = runTest(testDispatcher) {
        advanceUntilIdle()

        assertEquals(0, viewModel.decksDB.value.size, "Deck list should be empty at the start")
        assertEquals(0, viewModel.cardsDB.value.size)
        assertEquals(0, viewModel.defCardList.size)
    }

    @Test
    fun checkValoCardDownloader() = runTest(testDispatcher) {
        advanceUntilIdle()

        if (viewModel.decksDB.value.isNotEmpty()) {
            viewModel.downloadCardsFromDeck(viewModel.decksDB.value[0])

            advanceUntilIdle()

            assertEquals(29, viewModel.cardsDB.value.size, message = "Valorant deck at 30/4/26 has 29 cards on it")
        }
    }

    @Test
    fun checkResetValuesAtStart() = runTest(testDispatcher) {
        advanceUntilIdle()
        viewModel.setStartGameValues()
        assertEquals(false, viewModel.isGameLost, )
        assertEquals(false, viewModel.isGameWon, )
    }

    @Test
    fun checkIndexesAtEnd() = runTest(testDispatcher) {
        advanceUntilIdle()
        viewModel.resetGame()
        assertEquals(emptyList(), viewModel.indexList, )
        assertEquals(emptyList(), viewModel.defCardList, )
    }

    @Test
    fun checkChangingGameWon() = runTest(testDispatcher) {
        advanceUntilIdle()
        viewModel.changeGameWon(false)
        assertEquals(false, viewModel.isGameWon, )
    }
}