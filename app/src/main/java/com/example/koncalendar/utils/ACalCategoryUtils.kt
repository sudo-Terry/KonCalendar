package com.example.koncalendar.utils

import android.util.Log
import com.example.koncalendar.models.CalendarCategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

object ACalCategoryUtils {

    private val firestore = FirebaseFirestore.getInstance()
    private const val TAG = "ACalCategoryUtils"

    fun createOrUpdate_ACalCategory(): CalendarCategory? {
        val calendarCategory = CalendarCategory(
            id = "0",   //0번 calendarCategory는 건국대 학사일정 카테고리로 고정
            userId = "konkuk",
            title = "konkuk_academic_calendar",
            createdAt = Timestamp.now()
        )

        val docRef = firestore.collection("calendar_category").document(calendarCategory.id)

        // Check if the document exists
        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // If the document exists, update it
                docRef.set(calendarCategory)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully updated!")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating document", e)
                    }
            } else {
                // If the document does not exist, create it
                docRef.set(calendarCategory)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully created!")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error creating document", e)
                    }
            }
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error getting document", e)
        }
        return calendarCategory
    }
}