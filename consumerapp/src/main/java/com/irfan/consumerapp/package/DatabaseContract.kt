package com.irfan.consumerapp.`package`

import android.net.Uri
import android.provider.BaseColumns

object DatabaseContract {
    private const val AUTHORITY = "com.irfan.githubuser"
    private const val SCHEME = "content"

    class UserColumns : BaseColumns {
        companion object {
            private const val TABLE_NAME = "users"
            const val AVATAR_URL = "avatar_url"
            const val COMPANY = "company"
            const val EMAIL = "email"
            const val FOLLOWERS = "followers"
            const val FOLLOWING = "following"
            const val HTML_URL = "html_url"
            const val ID = "id"
            const val LOCATION = "location"
            const val LOGIN = "login"
            const val NAME = "name"
            const val PUBLIC_REPOS = "public_repos"

            val CONTENT_URI : Uri = Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }
}