package com.wilker.bandeirax.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.wilker.bandeirax.entity.Session

@Dao
interface SessionDao {

    @Insert
    suspend fun insertSession(session: Session)

    @Query("SELECT * FROM session WHERE id = 0")
    suspend fun selectFirstSession() : Session?

    @Update
    suspend fun updateSession(session: Session)

    @Query("delete from session")
    suspend fun deleteAll()

}