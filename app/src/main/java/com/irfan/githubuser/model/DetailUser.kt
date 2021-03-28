package com.irfan.githubuser.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "users")
data class DetailUser(

    @ColumnInfo(name = "avatar_url")
    val avatar_url: String? = "",

    @ColumnInfo(name = "company")
    val company: String? = "",

    @ColumnInfo(name = "email")
    val email: String? = "",

    @ColumnInfo(name = "followers")
    val followers: Int? = -1,

    @ColumnInfo(name = "following")
    val following: Int? = -1,

    @ColumnInfo(name = "html_url")
    val html_url: String? = "",

    @ColumnInfo(name = "id")
    val id: Int? = -1,

    @ColumnInfo(name = "location")
    val location: String? = "",

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "login")
    @NonNull
    val login: String = "",

    @ColumnInfo(name = "name")
    val name: String? = "",

    @ColumnInfo(name = "public_repos")
    val public_repos: Int? = -1,
): Parcelable