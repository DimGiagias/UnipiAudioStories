package com.example.unipiaudiostories.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unipiaudiostories.data.model.Story
import com.example.unipiaudiostories.data.repository.StatsRepository
import com.example.unipiaudiostories.data.repository.StoryRepository
import com.example.unipiaudiostories.utils.TTSManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StoryDetailsViewModel(
    context: Context
) : ViewModel() {

    private val storyRepository = StoryRepository()
    private val statsRepository = StatsRepository()
    private val ttsManager = TTSManager(context)

    private val _story = MutableStateFlow<Story?>(null)
    val story: StateFlow<Story?> = _story

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _availableVoices = MutableStateFlow<List<String>>(emptyList())
    val availableVoices: StateFlow<List<String>> = _availableVoices

    private val _isTTSInitialized = MutableStateFlow(false)

    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate
    val progress: StateFlow<Float> = ttsManager.progress
    val totalDuration: StateFlow<Long> = ttsManager.totalDuration

    fun initializeTTS() {
        ttsManager.initialize { success ->
            _isTTSInitialized.value = success
            if (success) {
                _availableVoices.value = ttsManager.getAvailableVoices()
            }
        }
    }

    fun fetchStory(storyId: String) {
        viewModelScope.launch {
            if (_story.value != null) {
                Log.d("StoryDetailsViewModel", "Story already loaded, skipping fetch.")
                return@launch
            }

            _isLoading.value = true
            try {
                val fetchedStory = storyRepository.getStoryById(storyId)
                _story.value = fetchedStory
                Log.d("StoryDetailsViewModel", "Successfully fetched story: ${fetchedStory?.title}")
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load story."
            } finally {
                _isLoading.value = false
                Log.d("StoryDetailsViewModel", "Fetching story completed. isLoading = false")
            }
        }
    }

    // Increment story play count
    fun incrementPlayCount(storyId: String) {
        viewModelScope.launch {
            try {
                statsRepository.updateStoryStats(storyId)
                statsRepository.updateLastListenedStory(storyId)
            } catch (e: Exception) {
                // Handle exception (e.g., log error)
            }
        }
    }

    // Play story using TTS
    fun playStory(text: String, voiceName: String? = null) {
        ttsManager.speechRate = _speechRate.value
        ttsManager.speak(text, voiceName)
    }

    // Stop TTS
    fun stopStory() {
        ttsManager.stop()
    }

    // Set speech rate
    fun setSpeechRate(rate: Float) {
        _speechRate.value = rate
    }
}