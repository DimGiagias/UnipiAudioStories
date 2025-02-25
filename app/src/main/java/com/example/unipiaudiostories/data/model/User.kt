package com.example.unipiaudiostories.data.model

data class User(
    val name: String? = null,
    val email: String = "",
    val profileImageUrl: String? = null,
    val stats: UserStats? = null
)
