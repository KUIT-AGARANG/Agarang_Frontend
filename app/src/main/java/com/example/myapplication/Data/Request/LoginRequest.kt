package com.example.myapplication.Data.Request

data class BabyCodeRequest(
    val babyCode: String
)

data class BabyRequest(
    val babyName: String,
    val dueDate: String,
    val familyRole: String
)

data class FamilyRoleRequest(
    val familyRole: String
)