package com.capstone.models

import java.io.Serializable

data class User(
    val email: String,
    val firstName: String,
    val lastName: String,
    val id: String,
    val role: List<Role>,
    val username: String
) :Serializable