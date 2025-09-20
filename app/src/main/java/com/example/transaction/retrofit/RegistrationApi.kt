package com.example.transaction.retrofit

import retrofit2.http.Body
import retrofit2.http.POST

interface RegistrationApi {

    @POST("api/auth/register")
    suspend fun auth(@Body authRequest: RegRequest): User

}