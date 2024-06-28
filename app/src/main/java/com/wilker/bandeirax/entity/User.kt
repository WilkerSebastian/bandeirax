package com.wilker.bandeirax.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    var points: Int,
    val active: Boolean,
    val admin: Boolean
)
