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
import com.example.koncalendar.viewmodel.CalendarViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.*

@Composable
fun AddScheduleScreen(
    calendarViewModel: CalendarViewModel = viewModel(),
    user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") }
        )
        TextField(
            value = startDate,
            onValueChange = { startDate = it },
            label = { Text("Start Date (yyyy-MM-dd)") }
        )
        TextField(
            value = endDate,
            onValueChange = { endDate = it },
            label = { Text("End Date (yyyy-MM-dd)") }
        )
        Button(onClick = {
            val newSchedule = Schedule(
                id = UUID.randomUUID().toString(),
                startTime = "${startDate}T09:00:00", // 임시 시간 설정
                endTime = "${endDate}T17:00:00", // 임시 시간 설정
                startDate = startDate,
                endDate = endDate,
                title = title,
                categoryId = "", // 카테고리를 선택하는 UI를 추가해야 합니다.
                userId = user.uid,
                location = "",
                description = description,
                frequency = "none"
            )
            calendarViewModel.addSchedule(newSchedule)
        }) {
            Text("Add Schedule")
        }
    }
}
