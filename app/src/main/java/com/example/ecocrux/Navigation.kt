package com.example.ecocrux

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.ecocrux.ui.screens.DashboardScreen
import com.example.ecocrux.ui.screens.SplashScreen

@Composable
fun MainNavigation() {
  val backStack = rememberNavBackStack(Splash)

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider =
      entryProvider {
        entry<Splash> {
          SplashScreen(
            onNavigateToAuth = {
              backStack.removeLastOrNull()
              backStack.add(Auth)
            },
            onNavigateToDashboard = { 
              backStack.removeLastOrNull()
              backStack.add(Dashboard) 
            }
          )
        }
        entry<Auth> {
          com.example.ecocrux.ui.screens.AuthScreen(onNavigateToDashboard = { 
            backStack.removeLastOrNull()
            backStack.add(Dashboard) 
          })
        }
        entry<Dashboard> {
          DashboardScreen(onSignOut = {
            backStack.removeLastOrNull()
            backStack.add(Auth)
          })
        }
      },

  )
}
