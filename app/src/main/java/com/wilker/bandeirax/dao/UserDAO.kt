package com.wilker.bandeirax.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.wilker.bandeirax.entity.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>
}