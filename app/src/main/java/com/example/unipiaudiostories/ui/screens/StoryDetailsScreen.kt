package com.example.unipiaudiostories.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.unipiaudiostories.R
import com.example.unipiaudiostories.utils.transformGoogleDriveUrl
import com.example.unipiaudiostories.viewmodel.StoryDetailsViewModel

@Composable
fun StoryDetailsScreen(
    navController: NavHostController,
    storyId: String
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val scaleFactor = maxWidth / 360.dp  // Base width: 360dp (standard phone)
        val context = LocalContext.current
        val viewModel: StoryDetailsViewModel = remember { StoryDetailsViewModel(context) }

        val isLoading by viewModel.isLoading.collectAsState()
        val story by viewModel.story.collectAsState()
        val errorMessage by viewModel.errorMessage.collectAsState()
        val availableVoices by viewModel.availableVoices.collectAsState()
        val speechRate by viewModel.speechRate.collectAsState()
        val progress by viewModel.progress.collectAsState()
        val totalDuration by viewModel.totalDuration.collectAsState()

        val selectedVoice = remember { mutableStateOf<String?>(null) }
        val isDropdownExpanded = remember { mutableStateOf(false) }
        val elapsedTime = (progress * totalDuration).toLong()

        LaunchedEffect(storyId) {
            viewModel.fetchStory(storyId)
        }

        LaunchedEffect(Unit) {
            viewModel.initializeTTS()
        }

        // Stop TTS when this screen is removed from the composition.
        DisposableEffect(Unit) {
            onDispose {
                viewModel.stopStory()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding((16 * scaleFactor).dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy((16 * scaleFactor).dp)
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Text(text = stringResource(R.string.back), fontSize = (16 * scaleFactor).sp)
                }

                // Show a loading indicator, error message, or the content.
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    errorMessage != null -> {
                        Text(
                            text = errorMessage.orEmpty(),
                            color = Color.Red,
                            fontSize = (14 * scaleFactor).sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    else -> {
                        story?.let { story ->
                            // Card for Story Image.
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape((12 * scaleFactor).dp),
                                elevation = CardDefaults.cardElevation(8.dp)
                            ) {
                                AsyncImage(
                                    model = transformGoogleDriveUrl(story.imageUrl),
                                    contentDescription = "Story Image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height((300 * scaleFactor).dp),
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(R.drawable.image_placeholder),
                                    error = painterResource(R.drawable.error_image_placeholder)
                                )
                            }

                            // Card for Reading Progress.
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding((16 * scaleFactor).dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(stringResource(R.string.reading_progress), fontSize = (16 * scaleFactor).sp)
                                    LinearProgressIndicator(
                                        progress = progress,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = (8 * scaleFactor).dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.elapsed) +": ${elapsedTime / 1000}s / ${totalDuration / 1000}s",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = (14 * scaleFactor).sp
                                        )
                                    )
                                }
                            }

                            // Card for Play & Stop Buttons.
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding((16 * scaleFactor).dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(onClick = {
                                        viewModel.playStory(story.content, selectedVoice.value)
                                        viewModel.incrementPlayCount(storyId)
                                    }) {
                                        Text(stringResource(R.string.play), fontSize = (16 * scaleFactor).sp)
                                    }
                                    Button(onClick = { viewModel.stopStory() }) {
                                        Text(stringResource(R.string.stop), fontSize = (16 * scaleFactor).sp)
                                    }
                                }
                            }

                            // Card for Voice Selection & Speed Control.
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding((16 * scaleFactor).dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        // Voice selection column.
                                        Column {
                                            Text(stringResource(R.string.voice), fontSize = (14 * scaleFactor).sp)
                                            Box {
                                                Button(onClick = { isDropdownExpanded.value = true }) {
                                                    Text(
                                                        text = selectedVoice.value ?: stringResource(R.string.choose_a_voice),
                                                        fontSize = (14 * scaleFactor).sp
                                                    )
                                                }
                                                DropdownMenu(
                                                    expanded = isDropdownExpanded.value,
                                                    onDismissRequest = { isDropdownExpanded.value = false }
                                                ) {
                                                    availableVoices.forEachIndexed { index, voiceName ->
                                                        DropdownMenuItem(
                                                            onClick = {
                                                                selectedVoice.value = voiceName
                                                                isDropdownExpanded.value = false
                                                            },
                                                            text = {
                                                                Text(
                                                                    text = "Voice ${index + 1}",
                                                                    fontSize = (14 * scaleFactor).sp
                                                                )
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // Speed control column.
                                        Column {
                                            Text(stringResource(R.string.speed), fontSize = (14 * scaleFactor).sp)
                                            Slider(
                                                value = speechRate,
                                                onValueChange = { viewModel.setSpeechRate(it) },
                                                valueRange = 0.5f..2.0f,
                                                steps = 3,
                                                modifier = Modifier.width((150 * scaleFactor).dp)
                                            )
                                            Text(
                                                text = "x${String.format("%.1f", speechRate)}",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontSize = (14 * scaleFactor).sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            // Card for Story Title and Content.
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding((16 * scaleFactor).dp)) {
                                    Text(
                                        text = story.title,
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontSize = (22 * scaleFactor).sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height((8 * scaleFactor).dp))
                                    Text(
                                        text = story.content,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = (16 * scaleFactor).sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
