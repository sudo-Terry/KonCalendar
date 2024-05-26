package com.example.koncalendar

import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AuthOrMainScreen(auth: FirebaseAuth) {
    var user by remember { mutableStateOf(auth.currentUser) }

    if (user == null) {
        AuthScreen(
            auth = auth,
            onSignedIn = { signedInUser ->
                user = signedInUser
            }
        )
    } else {
        MainScreen(
            user = user!!,
            onSignOut = {
                auth.signOut()
                user = null
            }
        )
    }
}
