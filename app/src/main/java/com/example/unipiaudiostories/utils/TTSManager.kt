package com.example.unipiaudiostories.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import kotlin.math.max

/**
 * A Text-to-Speech (TTS) manager that wraps the Android [TextToSpeech] API.
 *
 * This class provides functionality to initialize TTS, speak text with an optional voice,
 * estimate the duration of speech, track progress, retrieve available voices, and shut down the TTS engine.
 *
 * @property context The [Context] used to create the [TextToSpeech] instance.
 * @property speechRate The multiplier for speech rate (default is 1.0).
 * @property progress A [StateFlow] representing the current progress of the speech (from 0.0 to 1.0).
 * @property totalDuration A [StateFlow] representing the estimated total duration (in milliseconds) to speak the text.
 */
class TTSManager(private val context: Context) {
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    var speechRate: Float = 1.0f

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private val _totalDuration = MutableStateFlow(0L)
    val totalDuration: StateFlow<Long> = _totalDuration

    private var totalCharacters: Int = 0 // Total length of the text
    private val baseRate = 150f // Average reading speed in words per minute

    /**
     * Initializes the Text-to-Speech engine.
     *
     * When the TTS engine is successfully initialized, it sets the default locale and attaches an utterance listener.
     *
     * @param onInitialized A callback that receives `true` if initialization is successful, or `false` otherwise.
     */
    fun initialize(onInitialized: (Boolean) -> Unit) {
        textToSpeech = TextToSpeech(context) { status ->
            isInitialized = (status == TextToSpeech.SUCCESS)
            if (isInitialized) {
                textToSpeech?.language = Locale.getDefault()
                setupUtteranceListener()
            }
            onInitialized(isInitialized)
        }
    }

    /**
     * Calculates the estimated total duration to speak the given [text].
     *
     * The duration is estimated based on the average reading speed (base rate) and the current [speechRate].
     *
     * @param text The text whose duration is to be estimated.
     * @return The estimated duration in milliseconds.
     */
    fun calculateTotalDuration(text: String): Long {
        val wordCount = text.split("\\s+".toRegex()).size
        val wordsPerSecond = (baseRate / 60) * speechRate
        return ((wordCount / max(wordsPerSecond, 1f)) * 1000).toLong()
    }

    /**
     * Speaks the provided [text] using the TTS engine.
     *
     * If a [voiceName] is provided, the engine attempts to select that voice.
     * This method also calculates the total duration and updates the progress based on the text length.
     *
     * @param text The text to be spoken.
     * @param voiceName Optional name of the voice to use.
     */
    fun speak(text: String, voiceName: String? = null) {
        if (!isInitialized) return

        // Set the voice if specified
        voiceName?.let {
            val voice = textToSpeech?.voices?.find { it.name == voiceName }
            textToSpeech?.voice = voice
        }

        // Set speech rate
        textToSpeech?.setSpeechRate(speechRate)

        // Set the total number of characters
        totalCharacters = text.length

        // Calculate total duration
        _totalDuration.value = calculateTotalDuration(text)

        // Speak text with utterance ID for tracking progress
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UTTERANCE_ID")
    }

    /**
     * Retrieves a list of available voice names.
     *
     * Only voices containing "network" in their name and matching the US locale are returned.
     *
     * @return A list of available voice names, or an empty list if none are available.
     */
    fun getAvailableVoices(): List<String> {
        return textToSpeech?.voices
            ?.filter { it.name.contains("network") && it.locale == Locale.US }
            ?.map { it.name }
            ?: emptyList()
    }

    /**
     * Stops the current TTS playback and resets the progress.
     */
    fun stop() {
        textToSpeech?.stop()
        _progress.value = 0f
    }

    /**
     * Shuts down the TTS engine, releasing its resources.
     */
    fun shutdown() {
        textToSpeech?.shutdown()
    }

    /**
     * Sets up the [UtteranceProgressListener] to track the progress of the speech synthesis.
     *
     * The listener updates the [_progress] value based on the range of characters spoken.
     */
    private fun setupUtteranceListener() {
        textToSpeech?.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _progress.value = 0f
                }

                override fun onDone(utteranceId: String?) {
                    _progress.value = 1f
                }

                override fun onError(utteranceId: String?) {
                    _progress.value = 0f
                }

                override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                    // Calculate progress based on character range
                    if (totalCharacters > 0) {
                        _progress.value = start.toFloat() / totalCharacters
                    }
                }
            }
        )
    }
}