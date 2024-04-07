package com.panoramagl.sample

import androidx.test.core.app.takeScreenshot
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SmokeTest {

    @get:Rule
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    var nameRule = TestName()

    @Test
    fun basicSmokeTest() {
        Thread.sleep(100)
        takeScreenshot().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-start")

        onView(withId(R.id.button_1)).perform(ViewActions.click())
        takeScreenshot().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-button1")
        onView(withId(R.id.button_2)).perform(ViewActions.click())
        takeScreenshot().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-button2")
    }

//    @Test
//    fun swipeTest() {
//        Thread.sleep(100)
//        takeScreenshot().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-start")
//
//        (0..10).forEach { x ->
//            // swipe up
//            (0..3).forEach { i ->
//                onView(allOf(withId(R.id.content_view), isDisplayed())).perform(swipeUp())
//            }
//
//            // swipe Down
//            (0..3).forEach { i ->
//                onView(allOf(withId(R.id.content_view), isDisplayed())).perform(swipeDown())
//            }
//        }
//        takeScreenshot().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-end")
//    }

}
