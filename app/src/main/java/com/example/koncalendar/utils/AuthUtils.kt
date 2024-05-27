package com.example.koncalendar.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

fun signIn(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSignedIn: (FirebaseUser) -> Unit,
    onSignInError: (String) -> Unit
) {

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSignedIn(auth.currentUser!!)
            } else {
                onSignInError("Invalid email or password")
            }
        }
}

fun signUp(
    auth: FirebaseAuth,
    email: String,
    password: String,
    firstName: String,
    lastName: String,
    onSignedIn: (FirebaseUser) -> Unit
) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val userProfile = hashMapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "email" to email
                )
                FirebaseFirestore.getInstance().collection("users").document(user!!.uid)
                    .set(userProfile)
                    .addOnSuccessListener { onSignedIn(user) }
                    .addOnFailureListener { /* Handle failure */ }
            } else {
                /* Handle sign-up failure */
            }
        }
}
