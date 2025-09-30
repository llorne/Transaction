package com.example.transaction.models

data class Vault (
    val name: String,
    val type: String,
    val balance: Double,
    val currency: Int,
    val status: Boolean
)