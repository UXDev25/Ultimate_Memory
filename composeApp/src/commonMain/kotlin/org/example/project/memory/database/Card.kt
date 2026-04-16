package org.example.project.memory.database

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val id: String,
    val deckId: String,
    val name: String,
    val imageUrl: String
)