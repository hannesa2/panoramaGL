package com.panoramagl.sample

import android.graphics.Bitmap
import androidx.test.core.app.takeScreenshot
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.captureToBitmap
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
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
        takeScreenshot().crop().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-start")
        onView(withId(R.id.content_view))
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-rootStart") })

        onView(withId(R.id.button_1)).perform(ViewActions.click())
        takeScreenshot().crop().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-button1")
        onView(withId(R.id.content_view))
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-rootB1") })
        onView(withId(R.id.button_2)).perform(ViewActions.click())
        takeScreenshot().crop().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-button2")
        onView(withId(R.id.content_view))
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-rootB2") })
    }

    //    @Test
//    fun swipeTest() {
//        Thread.sleep(100)
//        takeScreenshot().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-start")
//
//        repeat((0..10).count()) {
//            // swipe up
//            repeat((0..3).count()) {
//                onView(allOf(withId(R.id.content_view), isDisplayed())).perform(swipeUp())
//            }
//
//            // swipe Down
//            repeat((0..3).count()) {
//                onView(allOf(withId(R.id.content_view), isDisplayed())).perform(swipeDown())
//            }
//        }
//        takeScreenshot().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-end")
//    }

    fun Bitmap.crop() = Bitmap.createBitmap(this, 0, NOTIFY_AREA_HEIGHT, this.width, this.height - NOTIFY_AREA_HEIGHT)

    companion object {
        const val NOTIFY_AREA_HEIGHT = 20 // TODO ask system about this value
    }
}
