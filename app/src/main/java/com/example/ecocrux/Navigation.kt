package com.example.ecocrux

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.ecocrux.ui.screens.AuthScreen
import com.example.ecocrux.ui.screens.DashboardScreen
import com.example.ecocrux.ui.screens.FeedbackScreen
import com.example.ecocrux.ui.screens.MapScreen
import com.example.ecocrux.ui.screens.OnboardingScreen
import com.example.ecocrux.ui.screens.SplashScreen

@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(Splash)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Splash> {
                SplashScreen(
                    onNavigateToAuth = {
                        backStack.removeLastOrNull()
                        backStack.add(Auth)
                    },
                    onNavigateToDashboard = {
                        backStack.removeLastOrNull()
                        backStack.add(Dashboard)
                    },
                    onNavigateToOnboarding = {
                        backStack.removeLastOrNull()
                        backStack.add(Onboarding)
                    }
                )
            }
            entry<Auth> {
                AuthScreen(
                    onNavigateToDashboard = {
                        backStack.removeLastOrNull()
                        backStack.add(Dashboard)
                    },
                    onNavigateToOnboarding = {
                        backStack.removeLastOrNull()
                        backStack.add(Onboarding)
                    }
                )
            }
            entry<Onboarding> {
                OnboardingScreen(
                    onComplete = {
                        backStack.removeLastOrNull()
                        backStack.add(Dashboard)
                    }
                )
            }
            entry<Dashboard> {
                DashboardScreen(
                    onSignOut = {
                        backStack.removeLastOrNull()
                        backStack.add(Auth)
                    },
                    onNavigateToFeedback = {
                        backStack.add(Feedback)
                    },
                    onNavigateToMap = {
                        backStack.add(Map)
                    }
                )
            }
            entry<Feedback> {
                FeedbackScreen(
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<Map> {
                MapScreen(
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        },
    )
}
