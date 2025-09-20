package com.example.transaction.retrofit

data class RegRequest(
    val username: String,
    val firstname: String,
    val lastname: String,
    val password: String,
    val confirmPassword: String
)
