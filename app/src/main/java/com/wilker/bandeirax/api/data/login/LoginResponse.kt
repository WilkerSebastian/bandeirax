package com.wilker.bandeirax.api.data.login

import com.wilker.bandeirax.entity.User

data class LoginResponse(
    val message: String,
    val data: User?
)
