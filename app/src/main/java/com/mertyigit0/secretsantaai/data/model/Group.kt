package com.mertyigit0.secretsantaai.data.model


data class Group(
    val groupId: String = "",
    val groupName: String = "",
    val members: List<String> = emptyList()
)
