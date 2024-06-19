package com.example.koncalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            AppContent(auth, navController)
        }
    }
}

@Composable
fun AppContent(auth: FirebaseAuth, navController: NavHostController) {
    var showSplashScreen by remember { mutableStateOf(true) }

    LaunchedEffect(showSplashScreen) {
        delay(2000)
        showSplashScreen = false
    }

    Crossfade(targetState = showSplashScreen, label = "") { isSplashScreenVisible ->
        if (isSplashScreenVisible) {
            SplashScreen {
                showSplashScreen = false
            }
        } else {
            NavHost(
                navController = navController,
                startDestination = if (auth.currentUser != null) "main" else "auth"
            ) {
                composable("auth") {
                    AuthScreen(auth) { user ->
                        navController.navigate("main") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                }
                composable("main") {
                    MainScreen(auth.currentUser!!, navController, onSignOut = {
                        auth.signOut()
                        navController.navigate("auth") {
                            popUpTo("main") { inclusive = true }
                        }
                    })
                }
                composable("categorySharing") { CategorySharingScreen() }
                composable("addSchedule") { AddScheduleScreen(navController) }
            }
        }
    }
}