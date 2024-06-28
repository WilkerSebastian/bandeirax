package com.wilker.bandeirax.api

import com.wilker.bandeirax.api.data.create.CreateRequest
import com.wilker.bandeirax.api.data.create.CreateResponse
import com.wilker.bandeirax.api.data.findOne.FindOneResponse
import com.wilker.bandeirax.api.data.login.LoginResponse
import com.wilker.bandeirax.api.data.update.UpdateRequest
import com.wilker.bandeirax.api.data.update.UpdateResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

const val API_KEY = "f713b550ebf67f719aac6ff6912b72d8"

interface ApiService {

    @POST("/user/create/$API_KEY")
    fun postCreateUser(@Body user: CreateRequest): Call<CreateResponse>

    @GET("/user/verified/login/$API_KEY")
    fun getVerfiedLogin(@Query("email") email: String,
                        @Query("password") password: String): Call<LoginResponse>

    @GET("/user/$API_KEY")
    fun findOneUser(@Query("id") id: String): Call<FindOneResponse>

    @PUT("/user/update/$API_KEY")
    fun putUser(@Body user: UpdateRequest): Call<UpdateResponse>

}
