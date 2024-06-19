package com.example.koncalendar.utils

import android.content.Context
import android.text.TextUtils.substring
import android.util.Log
import com.example.koncalendar.models.CalendarCategory
import com.example.koncalendar.models.Schedule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException

object ACalCategoryUtils {

    private val firestore = FirebaseFirestore.getInstance()
    private const val TAG = "ACalCategoryUtils"

    class AcManager(private val context: Context) {

        fun createAndLoadSchedules(userId: String) {
            CoroutineScope(Dispatchers.IO).launch {
                val category: CalendarCategory? = createOrUpdate_ACalCategory(userId)
                if (category != null) {
                    val schedules: List<Schedule>? = loadSchedulesFromAsset(context, userId)
                    schedules?.let {
                        saveSchedulesToFirestore(it)
                    }
                }
            }
        }
    }


    suspend fun createOrUpdate_ACalCategory(userId: String): CalendarCategory? {

        val calendarCategory = CalendarCategory(
            id = "",
            userId = userId,
            title = "konkuk_academic_calendar",
            createdAt = Timestamp.now()
        )

        return try {
            val querySnapshot = firestore.collection("calendar_category")
                .whereEqualTo("title", "konkuk_academic_calendar")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val docRef = if (querySnapshot.documents.isNotEmpty()) {
                querySnapshot.documents[0].reference
            } else {
                firestore.collection("calendar_category").document()
            }

            docRef.set(calendarCategory).await()
            Log.d(TAG, "DocumentSnapshot successfully written!")
            calendarCategory
        } catch (e: Exception) {
            Log.w(TAG, "Error creating or updating document", e)
            null
        }
    }

    suspend fun loadSchedulesFromAsset(context: Context, userId: String): List<Schedule>? {
        val categoryId = getCategoryId(userId) ?: return null

        val jsonString: String
        try {
            jsonString = context.assets.open("schedules.json")
                .bufferedReader()
                .use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        val gson = Gson()
        val listScheduleType = object : TypeToken<List<JsonObject>>() {}.type
        val jsonObjects: List<JsonObject> = gson.fromJson(jsonString, listScheduleType)

        return jsonObjects.map { jsonObject ->
            val start = jsonObject.get("Start").asString
            val end = jsonObject.get("End").asString
            val subject = jsonObject.get("Subject").asString
            val location = jsonObject.getAsJsonObject("Location").get("DisplayName").asString
            val startDate = start.substring(0, 10)
            val endDate = end.substring(0,10)

            Schedule(
                id = "",
                startDate = startDate,
                endDate = endDate,
                title = subject,
                categoryId = categoryId,
                userId = userId,
                location = location,
                startTime = "00:00:00",
                endTime = "00:00:00",
                frequency = "",
                description = ""
            )
        }
    }
    suspend fun saveSchedulesToFirestore(schedules: List<Schedule>) {
        val collectionRef = firestore.collection("schedules")

        schedules.forEach { schedule ->
            val existingSchedules = collectionRef
                .whereEqualTo("userId", schedule.userId)
                .whereEqualTo("categoryId", schedule.categoryId)
                .whereEqualTo("startDate", schedule.startDate)
                .whereEqualTo("endDate", schedule.endDate)
                .get()
                .await()

            if (existingSchedules.isEmpty) {
                collectionRef.add(schedule)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot successfully written with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error writing document", e)
                    }
            } else {
                Log.d(TAG, "Schedule with the same userId, categoryId, startDate, and endDate already exists.")
            }
        }
    }

    private suspend fun getCategoryId(userId: String): String? {
        return try {
            val querySnapshot = firestore.collection("calendar_category")
                .whereEqualTo("title", "konkuk_academic_calendar")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            if (querySnapshot.documents.isNotEmpty()) {
                querySnapshot.documents[0].id
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting categoryId", e)
            null
        }
    }



}