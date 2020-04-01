package com.capstone.api.response

data class OrganizationResponse (
    val name: String,
    val acct_mngr: User,
    val street_address: String,
    val zip: String,
    val city: String,
    val province_state: String,
    val country: String
)