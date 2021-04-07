package com.irfan.githubuser.db

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.irfan.githubuser.model.DetailUser

@Dao
interface UserDao {
    @Query("SELECT * from users")
    fun getAllUser() : Cursor

    @Insert(onConflict = REPLACE)
    fun insert(user: DetailUser) : Long

    @Query("SELECT * FROM users WHERE login = :username")
    fun getUserByUsername(username: String) : Cursor

    @Query("DELETE FROM users WHERE login = :username")
    fun delete(username: String) : Int

}