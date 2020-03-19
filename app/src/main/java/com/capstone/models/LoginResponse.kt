package com.capstone.models

data class LoginResponse (val accessToken: String, val tokenType: String, val user: User)