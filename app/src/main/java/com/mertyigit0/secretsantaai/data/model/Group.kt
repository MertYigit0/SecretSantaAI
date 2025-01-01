package com.mertyigit0.secretsantaai.data.model


data class Group(
    val groupId: String = "",
    val groupName: String = "",
    val members: List<String> = listOf(),
    val createdAt: String = "",
    val budget: Int = 0,
    val note: String = "",
    val date: String? = null // 'date' olarak değiştirildi
)



data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val groupsCreated: List<String> = emptyList(),
    val groupsJoined: List<String> = emptyList()
)
