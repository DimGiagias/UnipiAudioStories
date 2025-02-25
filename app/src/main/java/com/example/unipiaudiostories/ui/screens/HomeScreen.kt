package com.example.unipiaudiostories.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.unipiaudiostories.ui.components.StoryCard
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unipiaudiostories.viewmodel.HomeScreenViewModel

@Composable
fun HomeScreen(navController: NavHostController) {
    val viewModel: HomeScreenViewModel = viewModel()
    val stories by viewModel.stories.collectAsState()

    if (stories.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(stories) { story ->
                StoryCard(
                    story = story
                ) {
                    navController.navigate("details/${story.id}")
                }
            }
        }
    }
}

