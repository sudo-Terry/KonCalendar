package com.example.koncalendar

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.koncalendar.models.CalendarCategory
import com.example.koncalendar.models.CategorySharing
import com.example.koncalendar.models.Schedule
import com.example.koncalendar.utils.CalendarCategoryUtils
import kotlinx.coroutines.launch

@Composable
fun TestScreen(modifier: Modifier = Modifier) {
    var testCalendarCategory by remember { mutableStateOf<CalendarCategory?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val sampleCalendarCategory = CalendarCategory(
        "",
        "userId123",
        "Test Category"
    )

    val sampleCategorySharing = CategorySharing(
        "",
        "userId123",
        "targetUserId456",
        "targetCategoryId789"
    )

    // ISO 8601
    val sampleSchedule = Schedule(
        "",
        "2024-05-15T23:00:14",
        "2024-05-15T23:59:59",
        "2024-05-15",
        "2024-05-15",
        "Test Schedule",
        "categoryId123",
        "userId123",
        location = "Test Location",
        description = "Test Description",
        frequency = "daily" // weekly, monthly
    )

    Column(modifier) {
        Button(onClick = {
            coroutineScope.launch {
                val docRef = CalendarCategoryUtils.createCalendarCategory(sampleCalendarCategory)
                if (docRef != null) {
                    testCalendarCategory = CalendarCategoryUtils.getCalendarCategoryByDocName(docRef.id)
                }
            }
        }) {
            Text("Create and Fetch Calendar Category")
        }

        if (testCalendarCategory != null) {
            Button(onClick = {
                coroutineScope.launch {
                    val docName = testCalendarCategory!!.id
                    val updatedCategory = testCalendarCategory!!.copy(
                        userId = if (testCalendarCategory!!.userId == "userId123") "userId123123" else "userId123"
                    )

                    CalendarCategoryUtils.updateCalendarCategory(docName, updatedCategory)
                    testCalendarCategory = CalendarCategoryUtils.getCalendarCategoryByDocName(docName)
                }
            }) {
                Text("Update Calendar Category ${testCalendarCategory!!.id}")
            }
        }

        Text(text = "Category ID: ${testCalendarCategory?.id ?: "Loading..."}")
        Text(text = "User ID: ${testCalendarCategory?.userId ?: "Loading..."}")
        Text(text = "Title: ${testCalendarCategory?.title ?: "Loading..."}")
        Text(text = "Created At: ${testCalendarCategory?.createdAt ?: "Loading..."}")
    }
}
