package org.example.project.memory.screens

import kotlinx.serialization.Serializable
import org.example.project.memory.database.Card

@Serializable
data class CardItem(
    val id: Int,
    val card: Card,
    var isFlipped: Boolean = false
)