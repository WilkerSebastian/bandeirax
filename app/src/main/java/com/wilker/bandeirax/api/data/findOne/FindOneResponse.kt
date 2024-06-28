package com.wilker.bandeirax.api.data.findOne

import com.wilker.bandeirax.entity.User

data class FindOneResponse(
    val message: String,
    val data: User?
)
