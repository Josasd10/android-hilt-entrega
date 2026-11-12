package com.example.android.hilt.data

import org.junit.Assert.*
import org.junit.Test

class LoggerInMemoryDataSourceTest {
    @Test fun newestLogsComeFirstAndCanBeCleared() {
        val source = LoggerInMemoryDataSource()
        source.addLog("first")
        source.addLog("second")
        source.getAllLogs { assertEquals(listOf("second", "first"), it.map { log -> log.msg }) }
        source.removeLogs()
        source.getAllLogs { assertTrue(it.isEmpty()) }
    }

    @Test fun snapshotsAndIndependentInstancesDoNotShareChanges() {
        val first = LoggerInMemoryDataSource()
        val second = LoggerInMemoryDataSource()
        first.addLog("kept in snapshot")
        var snapshot = emptyList<Log>()
        first.getAllLogs { snapshot = it }
        first.removeLogs()
        assertEquals("kept in snapshot", snapshot.single().msg)
        second.getAllLogs { assertTrue(it.isEmpty()) }
    }
}
