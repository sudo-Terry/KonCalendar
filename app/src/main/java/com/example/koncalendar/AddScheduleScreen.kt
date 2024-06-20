package com.example.koncalendar

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextFieldColors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.koncalendar.models.Schedule
import com.example.koncalendar.viewmodel.CalendarViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
@Composable
fun showDatePickerDialog(
    context: Context,
    onDateSelected: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit
) {
    val datePickerDialog = android.app.DatePickerDialog(context, { _, year, month, dayOfMonth ->
        val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
        onDateSelected(selectedDate)
    }, LocalDate.now().year, LocalDate.now().monthValue - 1, LocalDate.now().dayOfMonth)

    datePickerDialog.setOnDismissListener { onDismissRequest() }
    datePickerDialog.show()
}

@Composable
fun showTimePickerDialog(context: Context, onTimeSelected: (LocalTime) -> Unit, onDismissRequest: () -> Unit) {
    val timePickerDialog = remember {
        TimePickerDialog(context, { _, hourOfDay, minute ->
            val selectedTime = LocalTime.of(hourOfDay, minute)
            onTimeSelected(selectedTime)
        }, LocalTime.now().hour, LocalTime.now().minute, true)
    }
    DisposableEffect(Unit) {
        timePickerDialog.show()
        onDispose { timePickerDialog.dismiss() }
    }
}

@Composable
fun AddScheduleScreen(
    navController: NavController,
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
    var isButtonClicked by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val isFormValid by derivedStateOf {
        title.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank() && startTime.isNotBlank() && endTime.isNotBlank()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { navController.popBackStack() },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(bottom = 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0XFFA5D6A7))
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                contentDescription = "Back arrow",
                modifier = Modifier.size(24.dp),
            )
        }
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("일정 제목 *") },
            isError = title.isBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        if (title.isBlank()) {
            Text(
                text = "일정 제목은 필수 항목입니다.",
                color = Color.Red,
                style = TextStyle(fontSize = 12.sp)
            )
        }
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("일정 설명") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        Button(onClick = { showStartDatePicker = true }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0XFFA5D6A7))
        ) {
            Text("시작일 설정")
        }
        if (startDate.isNotBlank()){
            Text("시작일: $startDate")
        }
        if (startDate.isBlank()) {
            Text(
                text = "시작일은 필수 항목입니다.",
                color = Color.Red,
                style = TextStyle(fontSize = 12.sp)
            )
        }
        Button(onClick = { showEndDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0XFFA5D6A7))
        ) {
            Text("종료일 설정")
        }
        if (endDate.isNotBlank()){
            Text("종료일: $endDate")
        }
        if (endDate.isBlank()) {
            Text(
                text = "종료일은 필수 항목입니다.",
                color = Color.Red,
                style = TextStyle(fontSize = 12.sp)
            )
        }
        Button(onClick = { showStartTimePicker = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0XFFA5D6A7))
        ) {
            Text("시작 시간 설정")
        }
        if (startTime.isNotBlank()){
            Text("시작 시간: $startTime")
        }
        if (startTime.isBlank()) {
            Text(
                text = "시작 시간은 필수 항목입니다.",
                color = Color.Red,
                style = TextStyle(fontSize = 12.sp)
            )
        }
        Button(onClick = { showEndTimePicker = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0XFFA5D6A7))
        ) {
            Text("종료 시간 설정")
        }
        if (endTime.isNotBlank()){
            Text("종료 시간: $endTime")
        }
        if (endTime.isBlank()) {
            Text(
                text = "종료 시간은 필수 항목입니다.",
                color = Color.Red,
                style = TextStyle(fontSize = 12.sp)
            )
        }
        TextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("위치") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        Button(
            onClick = {
                if (!isButtonClicked && isFormValid) {
                    isButtonClicked = true
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
                        calendarViewModel.addSchedule(schedule)
                        isButtonClicked = false
                        navController.popBackStack()
                    }
                }
            },
            enabled = isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0XFFA5D6A7))
        ) {
            Text("일정 추가하기")
        }
    }

    if (showStartDatePicker) {
        showDatePickerDialog(context, onDateSelected = { date ->
            startDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            showStartDatePicker = false
        }, onDismissRequest = { showStartDatePicker = false })
    }

    if (showEndDatePicker) {
        showDatePickerDialog(context, onDateSelected = { date ->
            endDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            showEndDatePicker = false
        }, onDismissRequest = { showEndDatePicker = false })
    }

    if (showStartTimePicker) {
        showTimePickerDialog(context, onTimeSelected = { time ->
            startTime = time.format(DateTimeFormatter.ISO_LOCAL_TIME)
            showStartTimePicker = false
        }, onDismissRequest = { showStartTimePicker = false })
    }

    if (showEndTimePicker) {
        showTimePickerDialog(context, onTimeSelected = { time ->
            endTime = time.format(DateTimeFormatter.ISO_LOCAL_TIME)
            showEndTimePicker = false
        }, onDismissRequest = { showEndTimePicker = false })
    }
}


