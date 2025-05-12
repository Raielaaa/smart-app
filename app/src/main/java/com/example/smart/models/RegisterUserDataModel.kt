package com.example.smart.models

data class RegisterUserDataModel(
    val userIDModel: String,
    val firstNameModel: String,
    val lastNameModel: String,
    val emailModel: String,
    val roleModel: String,
    val issuesSent: String,
    val profileImageLink: String
)