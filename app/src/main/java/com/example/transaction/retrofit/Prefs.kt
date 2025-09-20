package com.example.transaction.retrofit

import android.content.Context
import com.google.gson.Gson

fun saveJwt(context: Context, jwtWrapper: JwtWrapper) {
    val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    val json = Gson().toJson(jwtWrapper)
    prefs.edit().putString("jwt_token", json).apply()
}

fun loadJwt(context: Context): JwtWrapper? {
    val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    val json = prefs.getString("jwt_token", null) ?: return null
    return Gson().fromJson(json, JwtWrapper::class.java)
}
