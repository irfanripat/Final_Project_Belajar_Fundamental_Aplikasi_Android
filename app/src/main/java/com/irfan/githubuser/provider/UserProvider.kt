package com.irfan.githubuser.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.irfan.githubuser.db.UserDao
import com.irfan.githubuser.db.UserDatabase
import com.irfan.githubuser.model.fromContentValues
import java.lang.UnsupportedOperationException

class UserProvider : ContentProvider() {

    companion object {
        private const val AUTHORITY = "com.irfan.githubuser"
        private const val SCHEME = "content"
        private const val TABLE_NAME = "users"

        private lateinit var dao : UserDao
        private lateinit var database : UserDatabase

        val CONTENT_URI : Uri = Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()

        private const val USER = 1
        private const val USER_USERNAME = 2
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            uriMatcher.addURI(AUTHORITY, TABLE_NAME, USER)
            uriMatcher.addURI(AUTHORITY, "$TABLE_NAME/#", USER_USERNAME)
        }
    }

    override fun onCreate(): Boolean {
        database = UserDatabase.getInstance(context as Context)
        dao = database.userDao()
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        return when (uriMatcher.match(uri)) {
            USER -> dao.getAllUser()
            USER_USERNAME -> dao.getUserByUsername(uri.lastPathSegment.toString())
            else -> null
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val added: Long = when (USER) {
            uriMatcher.match(uri) -> dao.insert(fromContentValues(values))
            else -> 0L
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return Uri.parse("$CONTENT_URI/$added")
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException()
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val deleted: Int = when (USER_USERNAME) {
            uriMatcher.match(uri) -> dao.delete(uri.lastPathSegment.toString())
            else -> 0

        }
        context?.contentResolver?.notifyChange(CONTENT_URI, null)

        return deleted
    }
}