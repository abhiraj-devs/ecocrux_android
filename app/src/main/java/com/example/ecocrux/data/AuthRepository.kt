package com.example.ecocrux.data

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put

class AuthRepository {
    private val auth = SupabaseClient.client.auth

    suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserMetadata(
        name: String,
        username: String,
        vehicle: String,
        profileImage: String = ""
    ): Result<Unit> {
        return try {
            auth.updateUser {
                data = buildJsonObject {
                    put("name", name)
                    put("username", username)
                    put("vehicle", vehicle)
                    put("profileImage", profileImage)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Reads the current user's metadata and returns a [UserProfile], or null
     * if there is no active session.
     */
    fun getUserProfile(): UserProfile? {
        val user = auth.currentSessionOrNull()?.user ?: return null
        val metadata = user.userMetadata?.jsonObject
        return UserProfile.fromMetadata(metadata)
    }

    /**
     * Returns true if the current user has completed onboarding (i.e. has a
     * non-blank name in their metadata).
     */
    fun isProfileComplete(): Boolean {
        val profile = getUserProfile() ?: return false
        return profile.name.isNotBlank()
    }
}
