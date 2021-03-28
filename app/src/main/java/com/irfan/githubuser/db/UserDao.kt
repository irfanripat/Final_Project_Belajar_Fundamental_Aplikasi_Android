package com.irfan.githubuser.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.irfan.githubuser.model.DetailUser

@Dao
interface UserDao {
    @Query("SELECT * from users")
    fun getAllUser() : List<DetailUser>

    @Insert(onConflict = REPLACE)
    fun insert(user: DetailUser)

    @Query("SELECT * FROM users WHERE login = :username")
    fun getUserByUsername(username: String) : DetailUser?

    @Delete
    fun delete(user: DetailUser)


}