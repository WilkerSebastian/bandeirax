package com.wilker.bandeirax.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "session",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("userId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Session(
    val id: Int,
    val userID: String
)
