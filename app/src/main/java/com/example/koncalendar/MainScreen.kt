package com.example.koncalendar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.koncalendar.models.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun MainScreen(user: FirebaseUser, onSignOut: () -> Unit) {
    val userProfile = remember { mutableStateOf<User?>(null) }

    LaunchedEffect(user.uid) {
        val firestore = FirebaseFirestore.getInstance()
        val userDocRef = firestore.collection("users").document(user.uid)
        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val firstName = document.getString("firstName")
                    val lastName = document.getString("lastName")
                    userProfile.value = User(firstName, lastName, user.email ?: "")
                }
            }
            .addOnFailureListener { /* Handle failure */ }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        userProfile.value?.let {
            Text("Welcome, ${it.firstName} ${it.lastName}!")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onSignOut() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Sign Out")
        }
    }
}

