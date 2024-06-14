package com.example.koncalendar.utils

import android.util.Log
import com.example.koncalendar.models.Schedule
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

object ScheduleUtils {

    private val firestore = FirebaseFirestore.getInstance()
    private const val TAG = "ScheduleUtils"

    suspend fun createSchedule(schedule: Schedule): Schedule? {
        return try {
            val documentRef = firestore.collection("schedules")
                .add(schedule)
                .await()

            schedule.copy(id = documentRef.id)
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "Error creating schedule", e)
            null
        }
    }

    suspend fun getSchedules(userId: String): List<Schedule>? {
        return try {
            val querySnapshot = firestore.collection("schedules")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { document ->
                document.toObject(Schedule::class.java)?.apply {
                    id = document.id
                }
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "Error getting schedules", e)
            null
        }
    }

    suspend fun getScheduleByDocName(docName: String): Schedule? {
        return try {
            val documentRef = firestore.collection("schedules")
                .document(docName)
                .get()
                .await()

            documentRef.toObject(Schedule::class.java)?.apply {
                id = documentRef.id
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "Error getting schedule by document name", e)
            null
        }
    }

    suspend fun updateSchedule(docName: String, schedule: Schedule): Boolean {
        return try {
            firestore.collection("schedules")
                .document(docName)
                .set(schedule)
                .await()
            true
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "Error updating schedule", e)
            false
        }
    }

    suspend fun deleteSchedule(docName: String): Boolean {
        return try {
            firestore.collection("schedules")
                .document(docName)
                .delete()
                .await()
            true
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "Error deleting schedule", e)
            false
        }
    }
}
