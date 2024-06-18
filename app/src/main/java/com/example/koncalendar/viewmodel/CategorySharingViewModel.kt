package com.example.koncalendar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.koncalendar.models.CategorySharing
import com.example.koncalendar.utils.CategorySharingUtils
import kotlinx.coroutines.launch

class CategorySharingViewModel : ViewModel() {
    private val _categoryId = MutableLiveData<String>("")
    val categoryId: LiveData<String> = _categoryId

    private val _userId = MutableLiveData<String>("")
    val userId: LiveData<String> = _userId

    private val _targetUserId = MutableLiveData<String>("")
    val targetUserId: LiveData<String> = _targetUserId

    private val _sharedWith = MutableLiveData<List<CategorySharing>>()
    val sharedWith: LiveData<List<CategorySharing>> = _sharedWith

    fun setCategoryId(id: String) {
        _categoryId.value = id
    }

    fun setUserId(id: String) {
        _userId.value = id
    }

    fun setTargetUserId(id: String) {
        _targetUserId.value = id
    }

    fun shareCategory() {
        viewModelScope.launch {
            val newCategorySharing = CategorySharing(
                id = "",
                userId = userId.value ?: "",
                targetUserId = targetUserId.value ?: "",
                targetCategoryId = categoryId.value ?: "",
                createdAt = com.google.firebase.Timestamp.now()
            )
            CategorySharingUtils.createCategorySharing(newCategorySharing)
            loadSharedUsers()
        }
    }

    fun loadSharedUsers() {
        viewModelScope.launch {
            _sharedWith.value = CategorySharingUtils.getCategorySharings(_userId.value ?: "")
        }
    }
}
