package com.example.ecocrux

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Splash : NavKey
@Serializable data object Auth : NavKey
@Serializable data object Onboarding : NavKey
@Serializable
data object Dashboard : NavKey

@Serializable data object Feedback : NavKey
@Serializable data object Map : NavKey
