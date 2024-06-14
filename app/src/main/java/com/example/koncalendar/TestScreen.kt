package com.example.koncalendar

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.koncalendar.models.CalendarCategory
import com.example.koncalendar.models.CategorySharing
import com.example.koncalendar.models.Schedule
import com.example.koncalendar.utils.CalendarCategoryUtils
import com.example.koncalendar.utils.CategorySharingUtils
import com.example.koncalendar.utils.ScheduleUtils
import kotlinx.coroutines.launch

@Composable
fun TestScreen(modifier: Modifier = Modifier) {
    var testCalendarCategory by remember { mutableStateOf<CalendarCategory?>(null) }
    var testCategorySharing by remember { mutableStateOf<CategorySharing?>(null) }
    var testSchedule by remember { mutableStateOf<Schedule?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val sampleCalendarCategory = CalendarCategory(
        "",
        "userId123",
        "Test Category",
        color = Color.Black.toString()
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
                        color = if (testCalendarCategory!!.color == Color.Black.toString()) Color.White.toString() else Color.Black.toString()
                        ,userId = if (testCalendarCategory!!.userId == "userId123") "userId123123" else "userId123"
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
        Text(text = "Title: ${testCalendarCategory?.color ?: "Loading..."}")
        Text(text = "Created At: ${testCalendarCategory?.createdAt ?: "Loading..."}")

        Button(onClick = {
            coroutineScope.launch {
                val docRef = CategorySharingUtils.createCategorySharing(sampleCategorySharing)
                if (docRef != null) {
                    testCategorySharing = CategorySharingUtils.getCategorySharingByDocName(docRef.id)
                }
            }
        }) {
            Text("Create and Fetch Category Sharing")
        }

        // Update Category Sharing Button
        if (testCategorySharing != null) {
            Button(onClick = {
                coroutineScope.launch {
                    val docName = testCategorySharing!!.id
                    val updatedSharing = testCategorySharing!!.copy(
                        userId = if (testCategorySharing!!.userId == "userId123") "userId123123" else "userId123"
                    )

                    CategorySharingUtils.updateCategorySharing(docName, updatedSharing)
                    testCategorySharing = CategorySharingUtils.getCategorySharingByDocName(docName)
                }
            }) {
                Text("Update Category Sharing ${testCategorySharing!!.id}")
            }
        }

        Text(text = "Category Sharing ID: ${testCategorySharing?.id ?: "Loading..."}")
        Text(text = "User ID: ${testCategorySharing?.userId ?: "Loading..."}")
        Text(text = "Target User ID: ${testCategorySharing?.targetUserId ?: "Loading..."}")
        Text(text = "Target Category ID: ${testCategorySharing?.targetCategoryId ?: "Loading..."}")

        // Create and Fetch Schedule Button
        Button(onClick = {
            coroutineScope.launch {
                val docRef = ScheduleUtils.createSchedule(sampleSchedule)
                if (docRef != null) {
                    testSchedule = ScheduleUtils.getScheduleByDocName(docRef.id)
                }
            }
        }) {
            Text("Create and Fetch Schedule")
        }

        // Update Schedule Button
        if (testSchedule != null) {
            Button(onClick = {
                coroutineScope.launch {
                    val docName = testSchedule!!.id
                    val updatedSchedule = testSchedule!!.copy(
                        userId = if (testSchedule!!.userId == "userId123") "userId123123" else "userId123"
                    )

                    ScheduleUtils.updateSchedule(docName, updatedSchedule)
                    testSchedule = ScheduleUtils.getScheduleByDocName(docName)
                }
            }) {
                Text("Update Schedule ${testSchedule!!.id}")
            }
        }

        Text(text = "Schedule ID: ${testSchedule?.id ?: "Loading..."}")
        Text(text = "Start Time: ${testSchedule?.startTime ?: "Loading..."}")
        Text(text = "End Time: ${testSchedule?.endTime ?: "Loading..."}")
        Text(text = "Start Date: ${testSchedule?.startDate ?: "Loading..."}")
        Text(text = "End Date: ${testSchedule?.endDate ?: "Loading..."}")
        Text(text = "Title: ${testSchedule?.title ?: "Loading..."}")
        Text(text = "Category ID: ${testSchedule?.categoryId ?: "Loading..."}")
        Text(text = "User ID: ${testSchedule?.userId ?: "Loading..."}")
        Text(text = "Location: ${testSchedule?.location ?: "Loading..."}")
        Text(text = "Description: ${testSchedule?.description ?: "Loading..."}")
        Text(text = "Frequency: ${testSchedule?.frequency ?: "Loading..."}")
    }
}
