package com.example.a518lablearnandroid

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MenuActivityTest {

    // กำหนด Rule สำหรับทดสอบ Compose ภายใน MenuActivity
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MenuActivity>()

    @Test
    fun verifyAllButtonsAreDisplayed() {
        // ตรวจสอบว่าปุ่มเมนูต่างๆ แสดงผลอยู่บนหน้าจอหรือไม่
        composeTestRule.onNodeWithText("RPGCardActivity").assertExists()
        composeTestRule.onNodeWithText("PokedexActivity").assertExists()
        composeTestRule.onNodeWithText("LifeCycleComposeActivity").assertExists()
        composeTestRule.onNodeWithText("SharedPreferencesActivity").assertExists()
        composeTestRule.onNodeWithText("GalleryActivity").assertExists()
        composeTestRule.onNodeWithText("SensorActivity").assertExists()
    }
}
