package com.example.ecocrux.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth

object SupabaseClient {
    // TODO: Replace with your actual Supabase URL and Anon Key
    private const val SUPABASE_URL = "https://hagmxfzraxxymxmzzzxn.supabase.co"
    private const val SUPABASE_ANON_KEY = "sb_publishable_QzKBVIKLcevpPDaG8aj_TQ_Dno9UquV"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Auth)
    }
}
