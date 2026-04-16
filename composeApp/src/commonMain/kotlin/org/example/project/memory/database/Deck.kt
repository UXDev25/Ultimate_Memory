package org.example.project.memory.database

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.serialization.Serializable

@Serializable
data class Deck(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val creationDate: DateTimeUnit
)