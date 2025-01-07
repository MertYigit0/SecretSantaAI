package com.mertyigit0.secretsantaai.data.model

data class Group(
    val groupId: String = "",
    val groupName: String = "",
    val users: List<User> = emptyList(), // members yerine users
    val createdAt: String = "",
    val budget: Int = 0,
    val note: String = "",
    val date: String? = null,
    val drawResults: Map<String, String> = emptyMap() // Çekiliş sonuçları
)



data class User(
    val userId: String = "",
    val email: String = "",
    val username: String = "",
    val groupsCreated: List<String> = emptyList(),
    val groupsJoined: List<String> = emptyList()
)


