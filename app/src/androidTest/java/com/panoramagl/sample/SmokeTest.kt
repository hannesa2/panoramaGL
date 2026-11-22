package com.panoramagl.sample

import android.graphics.Bitmap
import androidx.test.core.app.takeScreenshot
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.allOf
import info.hannes.timber.DebugFormatTree
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.runner.RunWith
import timber.log.Timber


@RunWith(AndroidJUnit4::class)
class SmokeTest {

    @get:Rule
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    var nameRule = TestName()

    @Before
    fun setUp() {
        Timber.plant(DebugFormatTree())
    }

    @Test
    fun basicSmokeTest() {
        Thread.sleep(300)
        takeScreenshot().cropWithoutNotificationArea().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-start")
        onView(withId(R.id.button_1)).perform(ViewActions.click())
        takeScreenshot().cropWithoutNotificationArea().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-button1")
        onView(withId(R.id.button_2)).perform(ViewActions.click())
        takeScreenshot().cropWithoutNotificationArea().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-button2")
    }

    @Test
    fun swipeTest() {
        Thread.sleep(100)
        takeScreenshot().cropWithoutNotificationArea().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-start")
        Timber.d("Start")
        repeat((0..1).count()) { index ->
            Timber.d("repeat $index")
            repeat((0..1).count()) { swipeIndex ->
                onView(allOf(withId(R.id.content_view), isDisplayed())).perform(swipeUp())
                Timber.d("swipeUp $index $swipeIndex")
                takeScreenshot().cropWithoutNotificationArea()
                    .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-swipeUp-$index-$swipeIndex")
            }
            repeat((0..1).count()) { swipeIndex ->
                onView(allOf(withId(R.id.content_view), isDisplayed())).perform(swipeDown())
                Timber.d("swipeDown $index $swipeIndex")
                takeScreenshot().cropWithoutNotificationArea()
                    .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-swipeDown-$index-$swipeIndex")
            }

            repeat((0..1).count()) { swipeIndex ->
                onView(allOf(withId(R.id.content_view), isDisplayed())).perform(swipeLeft())
                Timber.d("swipeLeft $index $swipeIndex")
                takeScreenshot().cropWithoutNotificationArea()
                    .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-swipeLeft-$index-$swipeIndex")
            }
            repeat((0..1).count()) { swipeIndex ->
                onView(allOf(withId(R.id.content_view), isDisplayed())).perform(swipeRight())
                Timber.d("swipeRight $index $swipeIndex")
                takeScreenshot().cropWithoutNotificationArea()
                    .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-swipeRight-$index-$swipeIndex")
            }
        }
        takeScreenshot().cropWithoutNotificationArea().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-end")
        Timber.d("End")
    }

    fun Bitmap.cropWithoutNotificationArea() = Bitmap.createBitmap(this, 0, NOTIFY_AREA_HEIGHT, this.width, this.height - NOTIFY_AREA_HEIGHT)

    companion object {
        const val NOTIFY_AREA_HEIGHT = 20 // TODO ask system about this value
    }
}
