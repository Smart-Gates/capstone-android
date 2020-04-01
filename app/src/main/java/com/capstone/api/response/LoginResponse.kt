package com.capstone.api.response

data class LoginResponse (val accessToken: String, val tokenType: String, val user: User)