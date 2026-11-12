package com.example.android.hilt.contentprovider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.android.hilt.data.LogDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/** Exposes persisted Room logs; the UI's in-memory logs are separate. */
class LogsContentProvider : ContentProvider() {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface LogsContentProviderEntryPoint {
        fun logDao(): LogDao
    }

    private val matcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, "logs", LOGS)
        addURI(AUTHORITY, "logs/#", LOG)
    }

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val code = matcher.match(uri)
        require(code == LOGS || code == LOG) { "Unknown URI: $uri" }
        val appContext = checkNotNull(context).applicationContext
        val dao = getLogDao(appContext)
        val cursor = if (code == LOGS) dao.selectAllLogsCursor()
            else dao.selectLogById(ContentUris.parseId(uri))
        cursor?.setNotificationUri(appContext.contentResolver, uri)
        return cursor
    }

    private fun getLogDao(appContext: Context): LogDao =
        EntryPointAccessors.fromApplication(
            appContext, LogsContentProviderEntryPoint::class.java
        ).logDao()

    override fun insert(uri: Uri, values: ContentValues?): Uri? = readOnly()
    override fun update(uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<out String>?): Int = readOnly()
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = readOnly()
    override fun getType(uri: Uri): String = when (matcher.match(uri)) {
        LOGS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.logs"
        LOG -> "vnd.android.cursor.item/vnd.$AUTHORITY.logs"
        else -> throw IllegalArgumentException("Unknown URI: $uri")
    }

    private fun readOnly(): Nothing = throw UnsupportedOperationException("Only reading operations are allowed")

    companion object {
        const val AUTHORITY = "com.example.android.hilt.provider"
        private const val LOGS = 1
        private const val LOG = 2
    }
}
