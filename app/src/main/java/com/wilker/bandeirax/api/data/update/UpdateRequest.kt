package com.wilker.bandeirax.api.data.update

data class UpdateRequest(
    val id: String,
    val name: String,
    val email: String,
    val password: String
)
