package com.example.ecocrux.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.ecocrux.R
import io.github.jan.supabase.auth.auth

@Preview(showBackground = true, device = "id:pixel_8")
@Composable
fun SplashScreenPreview() {
    SplashScreen(onNavigateToAuth = {}, onNavigateToDashboard = {})
}

@Composable
fun SplashScreen(onNavigateToAuth: () -> Unit, onNavigateToDashboard: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_animation")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        delay(4000) 
        com.example.ecocrux.data.SupabaseClient.client.auth.awaitInitialization()
        val session = com.example.ecocrux.data.SupabaseClient.client.auth.currentSessionOrNull()
        if (session != null) {
            onNavigateToDashboard()
        } else {
            onNavigateToAuth()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F15)), 
        contentAlignment = Alignment.Center
    ) {
        // App Icon
        Image(
            painter = painterResource(id = R.drawable.ecocruxicon),
            contentDescription = "Ecocrux Logo",
            modifier = Modifier
                .size(240.dp)
                .scale(scale)
        )

        // Bottom area with Zylabix logo and Loader
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = Color(0xFF4ADE80),
                strokeWidth = 2.dp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "from",
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Image(
                painter = painterResource(id = R.drawable.zylabixlogo),
                contentDescription = "Zylabix Logo",
                modifier = Modifier
                    .fillMaxWidth(0.6f)
            )
        }
    }
}
