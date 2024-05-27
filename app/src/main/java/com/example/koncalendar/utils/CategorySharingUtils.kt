package com.example.koncalendar.utils

import com.example.koncalendar.models.CategorySharing
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

object CategorySharingUtils {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun createCategorySharing(categorySharing: CategorySharing): Boolean {
        return try {
            firestore.collection("category_sharing")
                .document("${categorySharing.userId}_${categorySharing.targetUserId}_${categorySharing.targetCategoryId}")
                .set(categorySharing)
                .await()
            true
        } catch (e: FirebaseFirestoreException) {
            false
        }
    }

    suspend fun getCategorySharing(userId: Int): QuerySnapshot? {
        return try {
            firestore.collection("category_sharing")
                .whereEqualTo("user_id", userId)
                .get()
                .await()
        } catch (e: FirebaseFirestoreException) {
            null
        }
    }

    suspend fun updateCategorySharing(categorySharing: CategorySharing): Boolean {
        return try {
            firestore.collection("category_sharing")
                .document("${categorySharing.userId}_${categorySharing.targetUserId}_${categorySharing.targetCategoryId}")
                .set(categorySharing)
                .await()
            true
        } catch (e: FirebaseFirestoreException) {
            false
        }
    }

    suspend fun deleteCategorySharing(userId: Int, targetUserId: Int, targetCategoryId: Int): Boolean {
        return try {
            firestore.collection("category_sharing")
                .document("${userId}_${targetUserId}_${targetCategoryId}")
                .delete()
                .await()
            true
        } catch (e: FirebaseFirestoreException) {
            false
        }
    }
}
