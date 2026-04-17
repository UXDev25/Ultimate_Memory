package org.example.project.memory.database

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Deck(
    val id: String,
    val name: String,
    val description: String,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("creation_date") val creationDate: String
)