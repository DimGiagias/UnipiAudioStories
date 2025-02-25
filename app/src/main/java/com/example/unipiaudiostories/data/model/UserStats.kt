package com.example.unipiaudiostories.data.model

data class UserStats(
    val storyStats: Map<String, Int> = emptyMap(),
    val lastListenedStoryId: String? = null,
)
