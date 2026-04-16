package org.example.project.memory.database

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

const val SUPABASE_URL = "https://bunsuieemrzjxdhunlrw.supabase.co"
const val SUPABASE_KEY = "sb_publishable_YoWWZC4aDz9nASqKKPvv4w_V0IaQTpD"

object MemoSupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Postgrest)
    }
}