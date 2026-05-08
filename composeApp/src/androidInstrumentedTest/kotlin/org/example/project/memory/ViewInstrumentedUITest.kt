package org.example.project.memory
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewInstrumentedUITest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        // Això intenta forçar una estratègia d'injecció d'esdeveniments més compatible
        // Si el problema persisteix, la millor opció és baixar el compileSdk a 34 o 35
    }

    @Test
    fun testEnterAndExitGame() {
        composeTestRule.onNodeWithTag("playButton_id").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("deckItem_id")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onAllNodesWithTag("deckItem_id")
            .onFirst()
            .performClick()
        composeTestRule.onNodeWithTag("surrender_id").performClick()
    }

}