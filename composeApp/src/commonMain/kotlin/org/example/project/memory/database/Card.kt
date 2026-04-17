package org.example.project.memory.database

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val id: String,
    @SerialName("deck_id") val deckId: String,
    val name: String,
    @SerialName("image_url") val imageUrl: String
)