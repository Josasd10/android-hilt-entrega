package com.example.android.hilt.data

import java.util.LinkedList
import javax.inject.Inject

class LoggerInMemoryDataSource @Inject constructor() : LoggerDataSource {
    private val logs = LinkedList<Log>()

    override fun addLog(msg: String) {
        logs.addFirst(Log(msg, System.currentTimeMillis()))
    }

    override fun getAllLogs(callback: (List<Log>) -> Unit) {
        callback(logs.toList())
    }

    override fun removeLogs() {
        logs.clear()
    }
}
