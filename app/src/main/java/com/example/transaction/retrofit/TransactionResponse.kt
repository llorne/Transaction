package com.example.transaction.retrofit

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.transaction.retrofit.PageResponse
data class TransactionResponse(
    val id: String,
    val userId: String,
    val amount: Int,
    val categoryId: String,
    val accountId: String,
    val description: String,
    val tags: List<String>
)

data class TransactionUpdateRequest(
    val amount: Int,
    val description: String
)

data class TransactionCreateRequest(
    val userId: String,
    val amount: Int,
    val type: String,
    val categoryId: String,
    val accountId: String,
    val description: String,
    val tags: List<String>
)

