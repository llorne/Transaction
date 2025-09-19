package com.example.transaction.retrofit

import retrofit2.http.GET
import retrofit2.http.Query

data class PageResponse<T>(
    val content: List<T>,
    val page: PageMetadata
)

data class PageMetadata(
    val size: Int,
    val number: Int,
    val totalElements: Int,
    val totalPages: Int
)



