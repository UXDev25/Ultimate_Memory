package org.example.project.memory.database

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlin.collections.emptyList

class DecksRepository {
    private val decks = MemoSupabaseClient.client.from("decks")
    private val cards = MemoSupabaseClient.client.from("cards")

    suspend fun GetAllDecks(): List<Deck> {
        return try {
            decks.select().decodeList<Deck>()
        } catch (e: Exception) {
            println("Error getting decks: ${e.message}")
            emptyList()
        }
    }

    suspend fun GetAllCardsFromDeck(deckId: String): List<Card> {
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