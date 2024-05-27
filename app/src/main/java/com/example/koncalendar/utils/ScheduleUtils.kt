package com.example.koncalendar.utils

import com.example.koncalendar.models.Schedule
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

object ScheduleUtils {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun createSchedule(schedule: Schedule): Boolean {
        return try {
            firestore.collection("schedule")
                .add(schedule)
                .await()
            true
        } catch (e: FirebaseFirestoreException) {
            false
        }
    }

    suspend fun getSchedules(userId: String): QuerySnapshot? {
        return try {
            firestore.collection("schedule")
                .whereEqualTo("userId", userId)
                .get()
                .await()
        } catch (e: FirebaseFirestoreException) {
            null
        }
    }

    suspend fun updateSchedule(scheduleId: String, schedule: Schedule): Boolean {
        return try {
            firestore.collection("schedule")
                .document(scheduleId)
                .set(schedule)
                .await()
            true
        } catch (e: FirebaseFirestoreException) {
            false
        }
    }

    suspend fun deleteSchedule(scheduleId: String): Boolean {
        return try {
            firestore.collection("schedule")
                .document(scheduleId)
                .delete()
                .await()
            true
        } catch (e: FirebaseFirestoreException) {
            false
        }
    }
}

