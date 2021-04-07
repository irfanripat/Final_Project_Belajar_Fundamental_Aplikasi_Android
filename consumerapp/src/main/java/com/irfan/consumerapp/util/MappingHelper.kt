package com.irfan.consumerapp.util

import android.database.Cursor
import com.irfan.consumerapp.model.DetailUser

object MappingHelper {

    fun mapCursorToArrayList(userCursor: Cursor?) : ArrayList<DetailUser> {
        val userList = ArrayList<DetailUser>()

        userCursor?.apply {
            while (moveToNext()) {
                val avatarUrl = getString(getColumnIndexOrThrow("avatar_url"))
                val company = getString(getColumnIndexOrThrow("company"))
                val email = getString(getColumnIndexOrThrow("email"))
                val followers = getInt(getColumnIndexOrThrow("followers"))
                val following = getInt(getColumnIndexOrThrow("following"))
                val htmlUrl = getString(getColumnIndexOrThrow("html_url"))
                val id = getInt(getColumnIndexOrThrow("id"))
                val location = getString(getColumnIndexOrThrow("location"))
                val login = getString(getColumnIndexOrThrow("login"))
                val name = getString(getColumnIndexOrThrow("name"))
                val publicRepos = getInt(getColumnIndexOrThrow("public_repos"))
                userList.add(DetailUser(avatarUrl, company, email, followers, following, htmlUrl, id, location, login, name, publicRepos))
            }
        }

        return userList
    }

    fun mapCursorToObject(userCursor: Cursor?) : DetailUser {
        var user  = DetailUser()
        userCursor?.apply {
            moveToFirst()
            val avatarUrl = getString(getColumnIndexOrThrow("avatar_url"))
            val company = getString(getColumnIndexOrThrow("company"))
            val email = getString(getColumnIndexOrThrow("email"))
            val followers = getInt(getColumnIndexOrThrow("followers"))
            val following = getInt(getColumnIndexOrThrow("following"))
            val htmlUrl = getString(getColumnIndexOrThrow("html_url"))
            val id = getInt(getColumnIndexOrThrow("id"))
            val location = getString(getColumnIndexOrThrow("location"))
            val login = getString(getColumnIndexOrThrow("login"))
            val name = getString(getColumnIndexOrThrow("name"))
            val publicRepos = getInt(getColumnIndexOrThrow("public_repos"))
            user =  DetailUser(avatarUrl, company, email, followers, following, htmlUrl, id, location, login, name, publicRepos)
        }
        return user
    }

}