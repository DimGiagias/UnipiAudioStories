package com.example.unipiaudiostories.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unipiaudiostories.data.model.Story
import com.example.unipiaudiostories.data.repository.StoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel : ViewModel() {
    private val storyRepository = StoryRepository()

    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories

    init {
        fetchStories()
    }

    private fun fetchStories() {
        viewModelScope.launch {
            storyRepository.getAllStories().collect { fetchedStories ->
                _stories.value = fetchedStories
            }
        }
    }
}