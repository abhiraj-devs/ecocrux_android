package com.example.ecocrux.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrux.data.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    /** Returning user with a complete profile → go to Dashboard */
    object Success : AuthState()
    /** New user or incomplete profile → go to Onboarding */
    object SuccessNewUser : AuthState()
    data class Error(val message: String) : AuthState()
}

private fun sanitizeErrorMessage(message: String?): String {
    if (message == null) return "An unknown error occurred"
    if (message.contains("Email not confirmed", ignoreCase = true) || message.contains("email_not_confirmed", ignoreCase = true)) {
        return "Email not verified. Please verify your email or disable email confirmations in Supabase."
    }
    if (message.contains("Invalid login credentials", ignoreCase = true)) {
        return "Invalid email or password."
    }
    if (message.contains("User already registered", ignoreCase = true)) {
        return "User already registered."
    }
    if (message.contains("<html", ignoreCase = true) || message.contains("<!DOCTYPE", ignoreCase = true)) {
        return "A network error occurred. Please try again."
    }
    return if (message.length > 100) message.substring(0, 100) + "..." else message
}

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun validatePassword(password: String): String? {
        if (password.length < 8) return "Password must be at least 8 characters"
        if (!password.any { it.isUpperCase() }) return "Password must contain at least one uppercase letter"
        if (!password.any { it.isLowerCase() }) return "Password must contain at least one lowercase letter"
        if (!password.any { it.isDigit() }) return "Password must contain at least one digit"
        if (!password.any { !it.isLetterOrDigit() }) return "Password must contain at least one special character"
        return null
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = repository.login(email, password)
            if (result.isSuccess) {
                // Check if the user has completed onboarding
                val profileComplete = withContext(Dispatchers.IO) {
                    repository.isProfileComplete()
                }
                _authState.value = if (profileComplete) AuthState.Success else AuthState.SuccessNewUser
            } else {
                _authState.value = AuthState.Error(sanitizeErrorMessage(result.exceptionOrNull()?.message))
            }
        }
    }

    fun signUp(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        val passwordError = validatePassword(password)
        if (passwordError != null) {
            _authState.value = AuthState.Error(passwordError)
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = repository.signUp(email, password)
            if (result.isSuccess) {
                // New sign-up always goes to onboarding
                _authState.value = AuthState.SuccessNewUser
            } else {
                _authState.value = AuthState.Error(sanitizeErrorMessage(result.exceptionOrNull()?.message))
            }
        }
    }
    
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
