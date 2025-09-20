package com.example.transaction.retrofit

fun Token.toJwtWrapper(): JwtWrapper {
    return JwtWrapper(
        jwtToken = JwtWrapper.JwtToken(
            accessToken = this.accessToken,
            refreshToken = this.refreshToken
        )
    )
}
