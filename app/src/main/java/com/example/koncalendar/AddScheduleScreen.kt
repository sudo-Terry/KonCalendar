package com.example.koncalendar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.koncalendar.models.Schedule
import com.example.koncalendar.utils.ScheduleUtils
import com.example.koncalendar.viewmodel.CalendarViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

@Composable
fun AddScheduleScreen(
    calendarViewModel: CalendarViewModel = viewModel(),
    user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )
        TextField(
            value = startDate,
            onValueChange = { startDate = it },
            label = { Text("Start Date (yyyy-MM-dd)") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )
        TextField(
            value = endDate,
            onValueChange = { endDate = it },
            label = { Text("End Date (yyyy-MM-dd)") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )
        TextField(
            value = startTime,
            onValueChange = { startTime = it },
            label = { Text("Start Time (HH:mm:ss)") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )
        TextField(
            value = endTime,
            onValueChange = { endTime = it },
            label = { Text("End Time (HH:mm:ss)") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )
        TextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )
        Button(
            onClick = {
                coroutineScope.launch {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                    val schedule = Schedule(
                        title = title,
                        description = description,
                        startDate = startDate,
                        endDate = endDate,
                        startTime = startTime,
                        endTime = endTime,
                        location = location,
                        userId = userId
                    )
                    val createSchedule = ScheduleUtils.createSchedule(schedule)
                    createSchedule?.let {
                        calendarViewModel.addSchedule(it)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Add Schedule")
        }
    }
}