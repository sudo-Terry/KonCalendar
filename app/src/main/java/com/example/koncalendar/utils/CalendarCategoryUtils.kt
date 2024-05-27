package com.example.koncalendar.utils

import com.example.koncalendar.models.CalendarCategory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

object CalendarCategoryUtils {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun createCalendarCategory(calendarCategory: CalendarCategory): Boolean {
        return try {
            firestore.collection("calendar_category")
                .add(calendarCategory)
                .await()
            true
        } catch (e: FirebaseFirestoreException) {
            false
        }
    }

    suspend fun getCalendarCategories(userId: Int): QuerySnapshot? {
        return try {
            firestore.collection("calendar_category")
                .whereEqualTo("user_id", userId)
                .get()
                .await()
        } catch (e: FirebaseFirestoreException) {
            null
        }
    }

    suspend fun updateCalendarCategory(calendarCategoryId: String, calendarCategory: CalendarCategory): Boolean {
        return try {
            firestore.collection("calendar_category")
                .document(calendarCategoryId)
                .set(calendarCategory)
                .await()
            true
        } catch (e: FirebaseFirestoreException) {
            false
        }
    }

    suspend fun deleteCalendarCategory(categoryId: String): Boolean {
        return try {
            firestore.collection("calendar_category")
                .document(categoryId)
                .delete()
                .await()
            true
        } catch (e: FirebaseFirestoreException) {
            false
        }
    }
}
