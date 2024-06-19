package com.example.koncalendar.utils

import android.util.Log
import com.example.koncalendar.models.CategorySharing
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

object CategorySharingUtils {

    private val firestore = FirebaseFirestore.getInstance()
    const val TAG = "CategorySharingUtils"

    suspend fun createCategorySharing(categorySharing: CategorySharing): CategorySharing? {
        return try {
            val documentRef = firestore.collection("category_sharing")
                .add(categorySharing)
                .await()

            categorySharing.copy(id = documentRef.id)
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "Error creating category sharing", e)
            null
        }
    }

    suspend fun getCategorySharings(userId: String): List<CategorySharing>? {
        return try {
            val querySnapshot = firestore.collection("category_sharing")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { document ->
                document.toObject(CategorySharing::class.java)?.apply {
                    id = document.id
                }
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "Error getting category sharings", e)
            null
        }
    }

    suspend fun getCategorySharingByDocName(docName: String): CategorySharing? {
        return try {
            val documentRef = firestore.collection("category_sharing")
                .document(docName)
                .get()
                .await()

            documentRef.toObject(CategorySharing::class.java)?.apply {
                id = documentRef.id
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "Error getting category sharing by document name", e)
            null
        }
    }

    suspend fun updateCategorySharing(docName: String, categorySharing: CategorySharing): Boolean {
        return try {
            firestore.collection("category_sharing")
                .document(docName)
                .set(categorySharing)
                .await()
            true
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "Error updating category sharing", e)
            false
        }
    }

    suspend fun deleteCategorySharing(docName: String): Boolean {
        return try {
            firestore.collection("category_sharing")
                .document(docName)
                .delete()
                .await()
            true
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "Error deleting category sharing", e)
            false
        }
    }
}
