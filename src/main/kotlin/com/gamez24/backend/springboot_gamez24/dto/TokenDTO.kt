package com.gamez24.backend.springboot_gamez24.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class TokenDTO(
    @field:JsonProperty("access_token")
    val accessToken: String,

    @field:JsonProperty("token_type")
    val tokenType: String = "bearer"
)