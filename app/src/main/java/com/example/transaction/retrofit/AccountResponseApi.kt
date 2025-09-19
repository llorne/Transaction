package com.example.transaction.retrofit

import retrofit2.http.GET
import retrofit2.http.Query

interface AccountResponseApi {

    @GET(" http://localhost:{ПОРТ}/api/{СЕРВИС}?size={РАЗМЕР}")
    fun getAccountResponseById(): AccountResponse

    interface ApiService {
        @GET("api/transactions")
        suspend fun getTransactions(
            @Query("page") page: Int,
            @Query("size") size: Int
        ): PageResponse<TransactionResponse>
    }

}