package com.example.transaction.retrofit

import java.time.Instant

data class Token(
    val accessToken: AccessToken,
    val refreshToken: RefreshToken
) {
    data class AccessToken(
        val accessToken: String,
        val expiresAt: String
    )

    data class RefreshToken(
        val refreshToken: String,
        val expiresAt: String
    )
}
