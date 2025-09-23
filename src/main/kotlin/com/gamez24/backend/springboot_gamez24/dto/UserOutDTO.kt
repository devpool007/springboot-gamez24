package com.gamez24.backend.springboot_gamez24.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserOutDTO(
    val id: Long? = null,
    val email: String,

    // For compatibility with your frontend expecting 'userid'
    @param:JsonProperty("userid")
    val userId: Long? = id
) {
    constructor(id: Long, email: String) : this(id, email, id)
}