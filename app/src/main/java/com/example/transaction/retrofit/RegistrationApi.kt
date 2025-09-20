package com.example.transaction.retrofit

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RegistrationApi {
    @POST("api/auth/register")
    suspend fun auth(@Body authRequest: RegRequest): Token
    @GET("api/auth/register")
    suspend fun getToken(): Token



}