//package com.example.koncalendar
//
//import androidx.compose.runtime.*
//import androidx.navigation.NavHostController
//import com.google.firebase.auth.FirebaseAuth
//
//@Composable
//fun AuthOrMainScreen(auth: FirebaseAuth, navController: NavHostController,context: Context) {
//    var user by remember { mutableStateOf(auth.currentUser) }
//
//    if (user == null) {
//        AuthScreen(auth) { signedInUser ->
//            user = signedInUser
//            navController.navigate("main") {
//                popUpTo("authOrMain") { inclusive = true }
//            }
//        }
//    } else {
//        MainScreen(user!!, navController, context, onSignOut = {
//            auth.signOut()
//            user = null
//            navController.navigate("authOrMain") {
//                popUpTo("authOrMain") { inclusive = true }
//            }
//        })
//    }
//}
