package com.mertyigit0.secretsantaai.data.model


data class Group(
    val groupId: String = "",
    val groupName: String = "",
    val createdBy: String = "",
    val members: List<String> = emptyList(),
    val createdAt: String = ""
)
data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val groupsCreated: List<String> = emptyList(),
    val groupsJoined: List<String> = emptyList()
)
