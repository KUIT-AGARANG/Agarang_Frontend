package com.example.myapplication.Data.Response

data class FamilyResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: FamilyResult
)

data class FamilyResult(
    val babyCode: String,
    val members: List<FamilyMember>
)

data class FamilyMember(
    val memberId: Int,
    val providerId: String,
    val name: String,
    val role: String
)
