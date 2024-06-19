package com.example.koncalendar.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.koncalendar.models.CalendarCategory
import com.example.koncalendar.models.Schedule
import com.example.koncalendar.utils.CalendarCategoryUtils
import com.example.koncalendar.utils.CategorySharingUtils
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

    init {
        fetchCategories()
        fetchSchedules()
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
            Log.d(CategorySharingUtils.TAG, "Fetching categories for userId: $userId")
            val fetchedCategories = CalendarCategoryUtils.getCalendarCategories(userId) ?: emptyList()
            _categories.value = fetchedCategories
            Log.d(CategorySharingUtils.TAG, "Categories fetched: $fetchedCategories")
        }
    }

    fun fetchSchedules() {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "defaultUserId"
            Log.d(CategorySharingUtils.TAG, "Fetching schedules for userId: $userId")
            val fetchedSchedules = ScheduleUtils.getSchedules(userId) ?: emptyList()
            _schedules.value = fetchedSchedules
            Log.d(CategorySharingUtils.TAG, "Schedules fetched: $fetchedSchedules")
        }
    }

    fun addSchedule(schedule: Schedule) {
        viewModelScope.launch {
            ScheduleUtils.createSchedule(schedule)?.let { newSchedule ->
                val currentSchedules = _schedules.value.orEmpty().toMutableList()
                currentSchedules.add(newSchedule)
                _schedules.value = currentSchedules
                Log.d(CategorySharingUtils.TAG, "Schedule added: $newSchedule")
            }
        }
    }

    fun deleteSchedule(scheduleId: String) {
        viewModelScope.launch {
            if (ScheduleUtils.deleteSchedule(scheduleId)) {
                _schedules.value = _schedules.value?.filterNot { it.id == scheduleId }
                Log.d(CategorySharingUtils.TAG, "Schedule deleted: $scheduleId")
            }
        }
    }

    companion object {
        private const val TAG = "CalendarViewModel"
    }
}
