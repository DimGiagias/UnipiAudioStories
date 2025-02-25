package com.example.unipiaudiostories.data.repository

import android.util.Log
import com.example.unipiaudiostories.data.firebase.FirebaseService
import com.example.unipiaudiostories.data.model.Story
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class StoryRepository {

    private val storiesCollection = FirebaseService.firestore.collection("stories")

    fun getAllStories(): Flow<List<Story>> = flow {
        try {
            val snapshot = storiesCollection.get().await()
            val stories = snapshot.documents.mapNotNull { it.toObject(Story::class.java) }
            emit(stories)
        } catch (e: Exception) {
            Log.e("StoryRepository", "Error fetching stories", e)
            emit(emptyList())
        }
    }

    suspend fun getStoryById(storyId: String): Story? {
        var retries = 3
        while (retries > 0) {
            try {
                val snapshot = storiesCollection.document(storyId).get().await()
                return snapshot.toObject(Story::class.java)
            } catch (e: Exception) {
                retries--
                Log.e("StoryRepository", "Retrying... ($retries retries left)", e)
                if (retries == 0) throw e
            }
        }
        return null
    }
}