package com.example.koncalendar.utils

import android.util.Log
import com.example.koncalendar.models.CalendarCategory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

object CalendarCategoryUtils {

    private val firestore = FirebaseFirestore.getInstance()
    private const val TAG = "CalendarCategoryUtils"

    suspend fun createCalendarCategory(calendarCategory: CalendarCategory): CalendarCategory? {
        return try {
            val documentRef = firestore.collection("calendar_category")
                .add(calendarCategory)
                .await()

            calendarCategory.copy(id = documentRef.id)
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "Error creating calendar category", e)
            null
        }
    }

    suspend fun getCalendarCategories(userId: String): List<CalendarCategory>? {
        return try {
            val querySnapshot = firestore.collection("calendar_category")
                .whereEqualTo("user_id", userId)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { document ->
                document.toObject(CalendarCategory::class.java)?.apply {
                    id = document.id
                }
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "Error getting calendar categories", e)
            null
        }
    }

    suspend fun getCalendarCategoryByDocName(docName: String): CalendarCategory? {
        return try {
            val documentRef = firestore.collection("calendar_category")
                .document(docName)
                .get()
                .await()

            documentRef.toObject(CalendarCategory::class.java)?.apply {
                id = documentRef.id
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "Error getting calendar category", e)
            null
        }
    }

    suspend fun updateCalendarCategory(docName: String, calendarCategory: CalendarCategory): Boolean {
        return try {
            firestore.collection("calendar_category")
                .document(docName)
                .set(calendarCategory)
                .await()
            true
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "Error updating calendar category", e)
            false
        }
    }

    suspend fun deleteCalendarCategory(docName: String): Boolean {
        return try {
            firestore.collection("calendar_category")
                .document(docName)
                .delete()
                .await()
            true
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "Error deleting calendar category", e)
            false
        }
    }
}
