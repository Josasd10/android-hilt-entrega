/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.hilt

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.recyclerview.widget.RecyclerView
import com.example.android.hilt.ui.MainActivity
import org.hamcrest.Matchers.containsString
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Rule
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class AppTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Test
    fun happyPath() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.textView)).check(matches(isDisplayed()))
            onView(withId(R.id.button1)).perform(click())
            onView(withId(R.id.all_logs)).perform(click())
            onView(withText(containsString("Interaction with 'Button 1'")))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun deleteLogsClearsSharedActivityLogger() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.button2)).perform(click())
            onView(withId(R.id.all_logs)).perform(click())
            onView(withText(containsString("Interaction with 'Button 2'")))
                .check(matches(isDisplayed()))
            pressBack()
            onView(withId(R.id.delete_logs)).perform(click())
            onView(withId(R.id.all_logs)).perform(click())
            onView(withId(R.id.recycler_view)).check { view, error ->
                if (error != null) throw error
                assertEquals(0, (view as RecyclerView).adapter!!.itemCount)
            }
        }
    }

    @Test
    fun recreatingActivityStartsANewInMemoryLogger() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onView(withId(R.id.button3)).perform(click())
            onView(withId(R.id.all_logs)).perform(click())
            onView(withText(containsString("Interaction with 'Button 3'")))
                .check(matches(isDisplayed()))
            scenario.recreate()
            onView(withId(R.id.recycler_view)).check { view, error ->
                if (error != null) throw error
                assertEquals(0, (view as RecyclerView).adapter!!.itemCount)
            }
        }
    }
}
