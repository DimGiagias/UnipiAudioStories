package com.example.unipiaudiostories.data.repository

import com.example.unipiaudiostories.data.firebase.FirebaseService
import kotlinx.coroutines.tasks.await

class StatsRepository {

    private val firestore = FirebaseService.firestore
    private val auth = FirebaseService.auth

    // Update the play count for a story
    suspend fun updateStoryStats(storyId: String) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User must be logged in")
        val userStatsRef = firestore.collection("users").document(uid)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userStatsRef)
            val stats = snapshot.get("stats.storyStats") as? MutableMap<String, Long> ?: mutableMapOf()

            // Increment or initialize the story's play count
            val currentCount = stats[storyId] ?: 0
            stats[storyId] = currentCount + 1

            // Update the stats in Firestore
            transaction.update(userStatsRef, "stats.storyStats", stats)
        }.await()
    }

    // Update the last listened story ID
    suspend fun updateLastListenedStory(storyId: String) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User must be logged in")
        val userStatsRef = firestore.collection("users").document(uid)

        userStatsRef.update("stats.lastListenedStoryId", storyId).await()
    }
}