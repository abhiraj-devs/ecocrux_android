package com.example.ecocrux.data

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * Holds user profile data stored in Supabase user_metadata.
 */
data class UserProfile(
    val name: String = "",
    val username: String = "",
    val vehicle: String = "Tata Nexon EV",
    val profileImage: String = ""
) {
    /** First name for greetings — takes text before the first space. */
    val firstName: String
        get() = name.trim().split(" ").firstOrNull()?.ifBlank { null } ?: ""

    /** Initial letter for avatar fallback. */
    val initial: String
        get() = firstName.firstOrNull()?.uppercase() ?: "?"

    companion object {
        /**
         * Parse a [UserProfile] from Supabase user_metadata JSON.
         * Returns null if the JSON is null.
         */
        fun fromMetadata(metadata: JsonObject?): UserProfile? {
            if (metadata == null) return null
            return UserProfile(
                name = metadata["name"]?.jsonPrimitive?.contentOrNull ?: "",
                username = metadata["username"]?.jsonPrimitive?.contentOrNull ?: "",
                vehicle = metadata["vehicle"]?.jsonPrimitive?.contentOrNull ?: "Tata Nexon EV",
                profileImage = metadata["profileImage"]?.jsonPrimitive?.contentOrNull ?: ""
            )
        }
    }
}
