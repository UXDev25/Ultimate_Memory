package org.example.project.memory
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.example.project.memory.screens.MainMenuScr
import org.junit.Rule
import kotlin.test.Test

class ViewInstrumentedUITest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()


    @Test
    fun myFirstTest(){
        composeTestRule.setContent {
            //MainMenuScr()
        }
        composeTestRule.onNodeWithTag("")
    }

}