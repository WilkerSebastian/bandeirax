package com.wilker.bandeirax.api.data.create

data class CreateResponse(
    val message: String,
    val id: String?,
    val email: String?,
    val validate: String?
)
