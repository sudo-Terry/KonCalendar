package com.example.koncalendar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.koncalendar.models.CalendarCategory
import com.example.koncalendar.models.Schedule
import com.example.koncalendar.utils.CalendarCategoryUtils
import com.example.koncalendar.utils.ScheduleUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class CalendarViewModel : ViewModel() {
    private val _selectedView = MutableLiveData<String>("Monthly")
    val selectedView: LiveData<String> = _selectedView

    private val _schedules = MutableLiveData<List<Schedule>>()
    val schedules: LiveData<List<Schedule>> = _schedules

    private val _categories = MutableLiveData<List<CalendarCategory>>(emptyList())
    val categories: LiveData<List<CalendarCategory>> = _categories

    private val _selectedCategory = MutableLiveData<String?>(null)
    val selectedCategory: LiveData<String?> = _selectedCategory

    init{
        fetchCategories()
    }

    fun setView(view: String) {
        _selectedView.value = view
    }

    fun setCategory(categoryId: String?) {
        _selectedCategory.value = categoryId
        loadSchedulesByCategory(categoryId)
    }

    private fun loadSchedulesByCategory(categoryId: String?) {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "defaultUserId"
            _schedules.value = if (categoryId == null) {
                ScheduleUtils.getSchedules(userId)
            } else {
                ScheduleUtils.getSchedulesByCategory(categoryId)
            }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "defaultUserId"
            _categories.value = CalendarCategoryUtils.getCalendarCategories(userId) ?: emptyList()
        }
    }

    fun fetchSchedules() {
        viewModelScope.launch {
            //여기에 크롤링 된 데이터 불러오는 함수 추가
        }
    }

    fun addSchedule(schedule: Schedule) {
        viewModelScope.launch {
            ScheduleUtils.createSchedule(schedule)?.let { newSchedule ->
                _schedules.value = _schedules.value?.plus(newSchedule)
            }
        }
    }

    // 테스트 카테고리와 일정 설정 메서드
    fun setCategories(testCategories: List<CalendarCategory>) {
        _categories.value = testCategories
    }

    fun setSchedules(testSchedules: List<Schedule>) {
        _schedules.value = testSchedules
    }
}