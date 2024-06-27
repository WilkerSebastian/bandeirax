package com.wilker.bandeirax.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "session",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("userId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["userId"], unique = true)]
)
data class Session(
    @PrimaryKey val id: Int,
    val userId: String
)
