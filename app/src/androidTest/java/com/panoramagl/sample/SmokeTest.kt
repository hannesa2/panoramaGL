package com.panoramagl.sample

import android.Manifest
import androidx.test.core.app.takeScreenshot
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
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

    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.HIGH_SAMPLING_RATE_SENSORS
    )

    @Test
    fun basicSmokeTest() {
        Thread.sleep(100)
        takeScreenshot().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-start")

        Espresso.onView(withId(R.id.button_1)).perform(ViewActions.click())
        takeScreenshot().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-button1")
        Espresso.onView(withId(R.id.button_2)).perform(ViewActions.click())
        takeScreenshot().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-button2")
    }

}
