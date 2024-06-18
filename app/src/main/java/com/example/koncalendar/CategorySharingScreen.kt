package com.example.koncalendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.koncalendar.viewmodel.CategorySharingViewModel

@Composable
fun CategorySharingScreen(
    sharingViewModel: CategorySharingViewModel = viewModel()
) {
    var categoryId by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        TextField(
            value = categoryId,
            onValueChange = { categoryId = it },
            label = { Text("Category ID") }
        )
        TextField(
            value = userId,
            onValueChange = { userId = it },
            label = { Text("User ID") }
        )
        Button(onClick = {
            sharingViewModel.setCategoryId(categoryId)
            sharingViewModel.setUserId(userId)
            sharingViewModel.shareCategory()
        }) {
            Text("Share Category")
        }
        Text("Shared with:")
        LazyColumn {
            items(sharingViewModel.sharedWith.value ?: emptyList()) { sharing ->
                Text("User ID: ${sharing.userId}")
            }
        }
    }
}
