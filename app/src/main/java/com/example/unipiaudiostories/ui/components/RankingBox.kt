package com.example.unipiaudiostories.ui.components

import android.graphics.fonts.FontStyle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.unipiaudiostories.R
import com.example.unipiaudiostories.data.model.Story
import com.example.unipiaudiostories.data.repository.StoryRepository

@Composable
fun RankingBox(
    index: Int,
    storyId: String,
    playCount: Int,
    storyRepository: StoryRepository = StoryRepository()
) {
    var story by remember { mutableStateOf<Story?>(null) }

    LaunchedEffect(storyId) {
        story = storyRepository.getStoryById(storyId)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display medal icon for the top three; else just the ranking number.
        if (index < 3) {
            val medalColor = when (index) {
                0 -> Color(0xFFFFD700) // Gold
                1 -> Color(0xFFC0C0C0) // Silver
                else -> Color(0xFFCD7F32) // Bronze
            }
            Icon(
                imageVector = Icons.Filled.EmojiEvents,
                contentDescription = "Medal for rank ${index + 1}",
                tint = medalColor,
                modifier = Modifier.size(32.dp)
            )
        } else {
            Text(
                text = "${index + 1}",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.width(32.dp),
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        // Show the story title if available; otherwise show "Loading..."
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = story?.title ?: "Loading...",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(R.string.heard) + " " + playCount + " " + stringResource(R.string.times),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}