package com.example.transaction.models

import kotlinx.serialization.Serializable

data class Vault (
    val name: String,
    val type: String,
    val balance: Double,
    val currency: Int,
    val status: Boolean
): java.io.Serializable