package com.example.transaction.retrofit

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.transaction.retrofit.PageResponse

data class AccountResponse (
    val id: String,
    val name: String,
    val balance: Float,
    val currency: String,
    val type: String,
    val active: Boolean
)

data class AccountUpdateRequest(
    val name: String,
    val currency: String,
    val type: String
)

data class AccountCreateRequest(
    val userId: String,
    val name: String,
    val currency: String,
    val type: String
)


