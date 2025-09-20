package com.example.transaction.retrofit

data class JwtWrapper(
    val jwtToken: JwtToken
) {
    data class JwtToken(
        val accessToken: Token.AccessToken,
        val refreshToken: Token.RefreshToken
    )
}
