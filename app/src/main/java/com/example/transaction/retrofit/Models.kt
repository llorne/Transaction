package com.example.transaction.retrofit

data class TokenResponse(
    val accessToken: AccessToken,
    val refreshToken: RefreshToken
)

data class AccessToken(
    val accessToken: String,
    val expiresAt: String
)

data class RefreshToken(
    val refreshToken: String,
    val expiresAt: String
)

data class RegisterRequest(
    val email: String,
    val password: String
)
