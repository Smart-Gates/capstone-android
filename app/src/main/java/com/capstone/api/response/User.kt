package com.capstone.api.response

import java.io.Serializable

data class User(
    val email: String,
    val firstName: String,
    val lastName: String,
    val id: String,
    val role: List<Role>,
    val username: String
) :Serializable