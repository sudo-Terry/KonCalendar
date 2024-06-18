package com.example.koncalendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.koncalendar.models.CalendarCategory
import com.example.koncalendar.models.Schedule
import com.example.koncalendar.models.User
import com.example.koncalendar.viewmodel.CategorySharingViewModel
import com.example.koncalendar.viewmodel.CalendarViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    user: FirebaseUser,
    onSignOut: () -> Unit,
    calendarViewModel: CalendarViewModel = viewModel(),
    sharingViewModel: CategorySharingViewModel = viewModel()
) {
    val userProfile = remember { mutableStateOf<User?>(null) }
    val selectedView by calendarViewModel.selectedView.observeAsState("Monthly")
    val schedules by calendarViewModel.schedules.observeAsState(emptyList())
    val categories by calendarViewModel.categories.observeAsState(emptyList())
    val sharedWith by sharingViewModel.sharedWith.observeAsState(emptyList())
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedTab by remember{ mutableStateOf(0) }

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

    // 기본 테스트 카테고리 생성
    if (categories.isEmpty()) {
        calendarViewModel.setCategories(
            listOf(
                CalendarCategory(id = "1", userId = user.uid, title = "Test Category 1"),
                CalendarCategory(id = "2", userId = user.uid, title = "Test Category 2")
            )
        )
    }

    // 기본 테스트 일정 생성
    if (schedules.isEmpty()) {
        calendarViewModel.setSchedules(
            listOf(
                Schedule(
                    id = "1",
                    startTime = "2024-05-15T09:00:00",
                    endTime = "2024-05-15T10:00:00",
                    startDate = "2024-05-15",
                    endDate = "2024-05-15",
                    title = "Test Schedule 1",
                    categoryId = "1",
                    userId = user.uid,
                    location = "Location 1",
                    description = "Description 1",
                    frequency = "daily"
                ),
                Schedule(
                    id = "2",
                    startTime = "2024-05-16T11:00:00",
                    endTime = "2024-05-16T12:00:00",
                    startDate = "2024-05-16",
                    endDate = "2024-05-16",
                    title = "Test Schedule 2",
                    categoryId = "2",
                    userId = user.uid,
                    location = "Location 2",
                    description = "Description 2",
                    frequency = "weekly"
                )
            )
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column {
                    Text("일간, 주간, 월간", modifier = Modifier.padding(16.dp))
                    Button(onClick = { calendarViewModel.setView("Daily") }) { Text("일") }
                    Button(onClick = { calendarViewModel.setView("Weekly") }) { Text("주") }
                    Button(onClick = { calendarViewModel.setView("Monthly") }) { Text("월") }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("일정", modifier = Modifier.padding(16.dp))
                    categories.forEach { category ->
                        CategoryButton(category = category, viewModel = calendarViewModel)
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Button(onClick = {
                        scope.launch {
                            calendarViewModel.fetchSchedules()
                            drawerState.close()
                        }
                    }) {
                        Text("일정 내려받기")
                    }
                }
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Calendar") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        }
                    )
                },
                bottomBar = {
                    BottomNavigation {
                        BottomNavigationItem(
                            icon = { Icon(Icons.Default.Menu, contentDescription = "Categories") },
                            label = { Text("Categories") },
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 }
                        )
                        BottomNavigationItem(
                            icon = { Icon(Icons.Default.Menu, contentDescription = "Share Category") },
                            label = { Text("Share Category") },
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 }
                        )
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        //여기에 navigation 설정 해야 할거같은데.. 아직 못했어요..
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Schedule")
                    }
                },
                floatingActionButtonPosition = FabPosition.End,
                content = { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        if (selectedTab == 0) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .align(Alignment.Center)
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
                                when (selectedView) {
                                    "Daily" -> DailyView(schedules)
                                    "Weekly" -> WeeklyView(schedules)
                                    "Monthly" -> MonthlyView(schedules)
                                }
                            }
                        } else {
                            CategorySharingSection(viewModel = sharingViewModel)
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
fun CategorySharingSection(viewModel: CategorySharingViewModel) {
    var categoryId by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
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
            viewModel.setCategoryId(categoryId)
            viewModel.setUserId(userId)
            viewModel.shareCategory()
        }) {
            Text("Share Category")
        }
        Text("Shared with:")
        LazyColumn {
            items(viewModel.sharedWith.value ?: emptyList()) { sharing ->
                Text("User ID: ${sharing.userId}")
            }
        }
    }
}

@Composable
fun DailyView(schedules: List<Schedule>) {
    val groupedSchedules = schedules.groupBy { it.startDate }
    LazyColumn {
        groupedSchedules.forEach { (date, schedules) ->
            item {
                Text(text = "Date: $date")
            }
            items(schedules) { schedule ->
                Text(text = "${schedule.title}: ${schedule.description}")
            }
        }
    }
}

@Composable
fun WeeklyView(schedules: List<Schedule>) {
    val groupedSchedules = schedules.groupBy { getWeekOfYear(it.startDate) }
    LazyColumn {
        groupedSchedules.forEach { (week, schedules) ->
            item {
                Text(text = "Week: $week")
            }
            items(schedules) { schedule ->
                Text(text = "${schedule.title}: ${schedule.description}")
            }
        }
    }
}

fun getWeekOfYear(dateString: String): Int {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date = LocalDate.parse(dateString, formatter)
    val weekFields = WeekFields.of(Locale.getDefault())
    return date.get(weekFields.weekOfWeekBasedYear())
}

@Composable
fun MonthlyView(schedules: List<Schedule>) {
    val groupedSchedules = schedules.groupBy { it.startDate.substring(0, 7) }
    LazyColumn {
        groupedSchedules.forEach { (month, schedules) ->
            item {
                Text(text = "Month: $month")
            }
            items(schedules) { schedule ->
                Text(text = "${schedule.title}: ${schedule.description}")
            }
        }
    }
}

