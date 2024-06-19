package com.example.koncalendar

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.koncalendar.models.CalendarCategory
import com.example.koncalendar.models.Schedule
import com.example.koncalendar.models.User
import com.example.koncalendar.utils.ACalCategoryUtils
import com.example.koncalendar.viewmodel.CategorySharingViewModel
import com.example.koncalendar.viewmodel.CalendarViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.WeekDayPosition
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    user: FirebaseUser,
    navController: NavHostController,
    context: Context,
    onSignOut: () -> Unit,
    calendarViewModel: CalendarViewModel = viewModel(),
    sharingViewModel: CategorySharingViewModel = viewModel()
) {
    val userProfile = remember { mutableStateOf<User?>(null) }
    val selectedView by calendarViewModel.selectedView.observeAsState("Monthly")
    val schedules by calendarViewModel.schedules.observeAsState(emptyList())
    val categories by calendarViewModel.categories.observeAsState(emptyList())
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val acManager = ACalCategoryUtils.AcManager(context)

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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet (
                modifier = Modifier.width(280.dp)
            ){
                Column (
                    modifier = Modifier.weight(1f)
                ) {
                    Text("캘린더 레이아웃 선택", modifier = Modifier.padding(16.dp))
                    Button(onClick = {
                        calendarViewModel.setView("Daily")
                        scope.launch {
                            drawerState.close()
                        }
                    }) { Text("Daily View") }
                    Button(onClick = {
                        calendarViewModel.setView("Monthly")
                        scope.launch {
                            drawerState.close()
                        }
                    }) { Text("Monthly View") }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("일정", modifier = Modifier.padding(16.dp))
                    categories.forEach { category ->
                        CategoryButton(category = category, viewModel = calendarViewModel)
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Button(onClick = {
                        scope.launch {
                            acManager.createAndLoadSchedules(userId = user.uid)
                            calendarViewModel.fetchSchedules()
                            drawerState.close()
                        }
                    }) {
                        Text("학사일정 내려받기")
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Button(onClick = {
                        navController.navigate("categorySharing")
                        scope.launch {
                            drawerState.close()
                        }
                    }) {
                        Text("사용자간 카테고리 공유")
                    }
                }
                Column {
//                    Button(
//                        onClick = { onSignOut() },
//                    ) {
//                        Text("로그아웃")
//                    }
                }
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("캘린더") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = { navController.navigate("addSchedule") }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Schedule")
                    }
                },
                content = { paddingValues ->
                    Column(Modifier.padding(paddingValues)) {
                        when (selectedView) {
                            "Daily" -> DayCalendarScreen(categories, schedules, selectedDate, onDateChange = { selectedDate = it }, viewModel = calendarViewModel)
                            "Weekly" -> WeekCalendarScreen(categories, schedules, selectedDate, onDateChange = { selectedDate = it }, viewModel = calendarViewModel)
                            "Monthly" -> MonthCalendarScreen(categories, schedules, selectedDate, onDateChange = { selectedDate = it }, viewModel = calendarViewModel)
                        }
                    }
                }
            )
        }
    )
}

@Composable
fun CategoryButton(category: CalendarCategory, viewModel: CalendarViewModel) {
    Button(onClick = { viewModel.setCategory(category.id) }) {
        Text(category.title)
    }
}

@Composable
fun CalendarTabs(
    categories: List<CalendarCategory>,
    schedules: List<Schedule>,
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    viewModel: CalendarViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }

    Column {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) { Text("일간") }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) { Text("주간") }
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) { Text("월간") }
        }
        when (selectedTab) {
            0 -> DayCalendarScreen(categories, schedules, selectedDate, onDateChange, viewModel)
            1 -> WeekCalendarScreen(categories, schedules, selectedDate, onDateChange, viewModel)
            2 -> MonthCalendarScreen(categories, schedules, selectedDate, onDateChange, viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayCalendarScreen(
    categories: List<CalendarCategory>,
    schedules: List<Schedule>,
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    viewModel: CalendarViewModel
) {
    Column {
        TopAppBar(
            title = { Text(selectedDate.toString()) },
            actions = {
                IconButton(onClick = { onDateChange(selectedDate.minusDays(1)) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Previous Day")
                }
                IconButton(onClick = { onDateChange(selectedDate.plusDays(1)) }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next Day")
                }
            }
        )
        LazyColumn {
            items(schedules.filter { it.startDate == selectedDate.toString() }) { schedule ->
                ScheduleItem(
                    schedule,
                    categories.find { it.id == schedule.categoryId }?.color,
                    viewModel
                )
            }
        }
    }
}

@Composable
fun WeekCalendarScreen(
    categories: List<CalendarCategory>,
    schedules: List<Schedule>,
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    viewModel: CalendarViewModel
) {
    val state = rememberWeekCalendarState(
        startDate = selectedDate.with(DayOfWeek.MONDAY),
        endDate = selectedDate.with(DayOfWeek.SUNDAY),
        firstVisibleWeekDate = selectedDate,
        firstDayOfWeek = DayOfWeek.SUNDAY
    )

    var selectedDay by remember { mutableStateOf(selectedDate) }

    Column {
        Text(
            text = "${state.startDate.month} ${state.startDate.year}",
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
            modifier = Modifier.padding(8.dp)
        )
        WeekCalendar(
            state = state,
            dayContent = { day ->
                DayContentForWeek(day, schedules, categories, onDayClick = {
                    selectedDay = it
                    onDateChange(it)
                })
            }
        )
        LazyColumn {
            items(schedules.filter {
                LocalDate.parse(it.startDate) <= selectedDay && LocalDate.parse(
                    it.endDate
                ) >= selectedDay
            }) { schedule ->
                ScheduleItem(
                    schedule,
                    categories.find { it.id == schedule.categoryId }?.color,
                    viewModel
                )
            }
        }
    }
}

@Composable
fun MonthCalendarScreen(
    categories: List<CalendarCategory>,
    schedules: List<Schedule>,
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    viewModel: CalendarViewModel
) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = DayOfWeek.SUNDAY
    )

    var selectedDay by remember { mutableStateOf(LocalDate.now()) }

    Column {
        Text(
            text = state.firstVisibleMonth.yearMonth.format(
                DateTimeFormatter.ofPattern(
                    "MMMM yyyy",
                    Locale.getDefault()
                )
            ),
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
            modifier = Modifier.padding(8.dp)
        )
        HorizontalCalendar(
            state = state,
            dayContent = { day ->
                DayContentForMonth(day, schedules, categories, onDayClick = {
                    selectedDay = it
                    onDateChange(it)
                })
            },
            monthHeader = { month ->
                MonthHeader(month)
            }
        )
        LazyColumn {
            items(schedules.filter {
                LocalDate.parse(it.startDate) <= selectedDay && LocalDate.parse(
                    it.endDate
                ) >= selectedDay
            }) { schedule ->
                ScheduleItem(
                    schedule,
                    categories.find { it.id == schedule.categoryId }?.color,
                    viewModel
                )
            }
        }
    }
}

@Composable
fun MonthHeader(month: CalendarMonth) {
    Row(modifier = Modifier.fillMaxWidth()) {
        month.weekDays.first().forEach { weekDay ->
            val dayOfWeek = weekDay.date.dayOfWeek
            Text(
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                text = dayOfWeek.getDisplayName(
                    java.time.format.TextStyle.SHORT,
                    java.util.Locale.getDefault()
                )
            )
        }
    }
}

@Composable
fun DayContentForMonth(
    day: CalendarDay,
    schedules: List<Schedule>,
    categories: List<CalendarCategory>,
    onDayClick: (LocalDate) -> Unit
) {
    val textColor = when (day.position) {
        //DayPosition.MonthDate -> if (isSelected) colorResource(R.color.example_6_month_bg_color) else Color.Unspecified
        DayPosition.MonthDate -> colorResource(R.color.example_6_black)
        DayPosition.InDate, DayPosition.OutDate -> colorResource(R.color.white)
    }

    val relevantSchedules = schedules.filter {
        LocalDate.parse(it.startDate) <= day.date && LocalDate.parse(it.endDate) >= day.date
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(color = colorResource(R.color.example_6_month_bg_color))
            .clickable(
                enabled = day.position == DayPosition.MonthDate,
                onClick = { onDayClick(day.date) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = TextStyle(fontWeight = FontWeight.Bold, color = textColor)
            )
            relevantSchedules.take(3).forEach { schedule ->
                val color = categories.find { it.id == schedule.categoryId }?.color?.let {
                    Color(
                        android.graphics.Color.parseColor(it)
                    )
                } ?: Color.Gray
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(color, MaterialTheme.shapes.small)
                )
            }
            if (relevantSchedules.size > 3) {
                Text(
                    "+${relevantSchedules.size - 3} more",
                    style = TextStyle(fontSize = 12.sp, color = Color.Gray)
                )
            }
        }
    }
}

@Composable
fun DayContentForWeek(
    day: WeekDay,
    schedules: List<Schedule>,
    categories: List<CalendarCategory>,
    onDayClick: (LocalDate) -> Unit
) {
    val isCurrentMonth = day.position == WeekDayPosition.RangeDate
    val textColor = if (isCurrentMonth) Color.Black else Color.Gray

    val relevantSchedules = schedules.filter {
        LocalDate.parse(it.startDate) <= day.date && LocalDate.parse(it.endDate) >= day.date
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .background(Color.LightGray, MaterialTheme.shapes.medium)
            .clickable { onDayClick(day.date) }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = TextStyle(fontWeight = FontWeight.Bold, color = textColor)
            )
            relevantSchedules.take(3).forEach { schedule ->
                val color = categories.find { it.id == schedule.categoryId }?.color?.let {
                    Color(
                        android.graphics.Color.parseColor(it)
                    )
                } ?: Color.Gray
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(color, MaterialTheme.shapes.small)
                )
            }
            if (relevantSchedules.size > 3) {
                Text(
                    "+${relevantSchedules.size - 3} more",
                    style = TextStyle(fontSize = 12.sp, color = Color.Gray)
                )
            }
        }
    }
}

@Composable
fun ScheduleItem(schedule: Schedule, colorHex: String?, viewModel: CalendarViewModel) {
    val color = colorHex?.let { Color(android.graphics.Color.parseColor(it)) } ?: Color.Gray
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(schedule.title, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp))
            Text("${schedule.startTime} - ${schedule.endTime}")
            if (schedule.location != null) Text("위치: ${schedule.location}")
            if (schedule.description != null) Text("설명: ${schedule.description}")

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { viewModel.deleteSchedule(schedule.id) }) {
                Text("Delete Schedule")
            }
        }
    }
}

