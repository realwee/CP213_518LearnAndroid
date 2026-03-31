package com.example.a518lablearnandroid

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    // กำหนด Rule สำหรับทดสอบ Compose ภายใน MainActivity
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun verifyInitialStatusValues() {
        // ตรวจสอบว่ามีค่าเริ่มต้นของสถานะตอนเปิดหน้าจอ
        composeTestRule.onNodeWithText("Str").assertIsDisplayed()
        composeTestRule.onNodeWithText("8").assertIsDisplayed()

        composeTestRule.onNodeWithText("Agi").assertIsDisplayed()
        composeTestRule.onNodeWithText("10").assertIsDisplayed()

        composeTestRule.onNodeWithText("Int").assertIsDisplayed()
        composeTestRule.onNodeWithText("15").assertIsDisplayed()
    }

    @Test
    fun testIncreaseStrDecreasesStr() {
        // กดปุ่มเพิ่ม Str
        composeTestRule.onNodeWithContentDescription("str_up").performClick()
        // ตรวจสอบว่า Str เพิ่มขึ้นเป็น 9
        composeTestRule.onNodeWithText("9").assertIsDisplayed()

        // กดปุ่มลด Str 2 ครั้ง
        composeTestRule.onNodeWithContentDescription("str_down").performClick()
        composeTestRule.onNodeWithContentDescription("str_down").performClick()
        // ตรวจสอบว่า Str ลดลงเป็น 7
        composeTestRule.onNodeWithText("7").assertIsDisplayed()
    }

    @Test
    fun testIncreaseAgiDecreasesAgi() {
        // กดปุ่มเพิ่ม Agi
        composeTestRule.onNodeWithContentDescription("agi_up").performClick()
        // ตรวจสอบว่า Agi เพิ่มขึ้นเป็น 11
        composeTestRule.onNodeWithText("11").assertIsDisplayed()

        // กดปุ่มลด Agi
        composeTestRule.onNodeWithContentDescription("agi_down").performClick()
        // ตรวจสอบว่า Agi กลับมาเป็น 10
        composeTestRule.onNodeWithText("10").assertIsDisplayed()
    }

    @Test
    fun testIncreaseIntDecreasesInt() {
        // กดปุ่มเพิ่ม Int 2 ครั้ง
        composeTestRule.onNodeWithContentDescription("int_up").performClick()
        composeTestRule.onNodeWithContentDescription("int_up").performClick()
        // ตรวจสอบว่า Int เพิ่มขึ้นเป็น 17
        composeTestRule.onNodeWithText("17").assertIsDisplayed()

        // กดปุ่มลด Int
        composeTestRule.onNodeWithContentDescription("int_down").performClick()
        // ตรวจสอบว่า Int ลดลงเหลือ 16
        composeTestRule.onNodeWithText("16").assertIsDisplayed()
    }
}
