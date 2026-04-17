package org.example.project.memory.database

import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlin.collections.emptyList

class DecksRepository {
    private val decks = MemoSupabaseClient.client.from("decks")
    private val cards = MemoSupabaseClient.client.from("cards")

    suspend fun getAllDecks(): List<Deck> {
        Napier.d(tag = "MEMORY_LOG") { "entering repository" }
        return try {
            val result = decks.select().decodeList<Deck>()
            Napier.d(tag = "MEMORY_LOG") { "Hem descarregat ${result.size} baralles!" }
            result
        } catch (e: Exception) {
            Napier.d(tag = "MEMORY_LOG") { "error getting decks: ${e.message}" }
            emptyList()
        }
    }

    suspend fun getAllCardsFromDeck(deckId: String): List<Card> {
        return try {
            cards.select {
                filter {
                    eq("deck_id", deckId)
                }
            }.decodeList<Card>()
        } catch (e: Exception) {
            println("Error getting cards: ${e.message}")
            emptyList()
        }
    }
}