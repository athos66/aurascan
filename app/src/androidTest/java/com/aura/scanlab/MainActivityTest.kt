package com.aura.scanlab

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testAppLoads() {
        // Since the camera permission is requested on start, 
        // the app should either show the camera screen (if granted) 
        // or the permission denial message (if denied).
        // This test just ensures the activity starts without crashing.
    }
}
