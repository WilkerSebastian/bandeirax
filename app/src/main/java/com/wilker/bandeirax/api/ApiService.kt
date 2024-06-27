package com.wilker.bandeirax.api

import com.wilker.bandeirax.api.data.create.CreateRequest
import com.wilker.bandeirax.api.data.create.CreateResponse
import com.wilker.bandeirax.api.data.login.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

const val API_KEY = "f713b550ebf67f719aac6ff6912b72d8"

interface ApiService {

    @POST("/user/create/$API_KEY")
    fun postCreateUser(@Body user: CreateRequest): Call<CreateResponse>

    @GET("/user/verified/login/$API_KEY")
    fun getVerfiedLogin(@Query("email") email: String,
                        @Query("password") password: String): Call<LoginResponse>

}
