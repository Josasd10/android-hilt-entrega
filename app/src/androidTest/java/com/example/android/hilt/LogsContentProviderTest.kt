package com.example.android.hilt

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.hilt.data.Log
import com.example.android.hilt.data.LogDao
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class LogsContentProviderTest {
    @get:Rule val hiltRule = HiltAndroidRule(this)
    @Inject lateinit var dao: LogDao
    private val resolver get() = ApplicationProvider.getApplicationContext<Context>().contentResolver
    private val uri = Uri.parse("content://com.example.android.hilt.provider/logs")

    @Before fun setUp() {
        hiltRule.inject()
        dao.nukeTable()
    }

    @After fun tearDown() {
        dao.nukeTable()
    }

    @Test fun queryAllAndOneLogThroughHiltEntryPoint() {
        dao.insertAll(Log("provider test", 1234L))
        val log = dao.getAll().single()
        resolver.query(uri, null, null, null, null)!!.use {
            assertEquals(1, it.count)
            assertTrue(it.moveToFirst())
            assertEquals(log.id, it.getLong(it.getColumnIndexOrThrow("id")))
        }
        resolver.query(ContentUris.withAppendedId(uri, log.id), null, null, null, null)!!.use {
            assertTrue(it.moveToFirst())
            assertEquals("provider test", it.getString(it.getColumnIndexOrThrow("msg")))
        }
        resolver.query(ContentUris.withAppendedId(uri, log.id + 1), null, null, null, null)!!.use {
            assertEquals(0, it.count)
        }
    }

    @Test(expected = UnsupportedOperationException::class)
    fun providerRejectsWrites() {
        resolver.insert(uri, ContentValues())
    }

    @Test(expected = IllegalArgumentException::class)
    fun providerRejectsUnknownUris() {
        resolver.query(Uri.parse("content://com.example.android.hilt.provider/unknown"), null, null, null, null)
    }
}
