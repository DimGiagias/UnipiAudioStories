package com.example.unipiaudiostories.ui.screens

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.unipiaudiostories.R
import com.example.unipiaudiostories.data.auth.AuthenticationManager
import com.example.unipiaudiostories.data.model.Story
import com.example.unipiaudiostories.data.model.User
import com.example.unipiaudiostories.data.repository.StoryRepository
import com.example.unipiaudiostories.utils.transformGoogleDriveUrl
import com.example.unipiaudiostories.viewmodel.ProfileViewModel
import java.util.Locale

@Composable
fun ProfileScreen(
    navController: NavController,
    authManager: AuthenticationManager = AuthenticationManager(),
    viewModel: ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    else if (errorMessage != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = errorMessage.orEmpty(), color = Color.Red)
        }
    }
    else if (userProfile != null) {
        UserProfileScreen(authManager = authManager, user = userProfile!!, navController = navController)
    }
    else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Text(text = "User not found.", color = Color.Red)
                Spacer(Modifier.padding(10.dp))
                Button(
                    onClick = {
                        authManager.signOut()
                        navController.navigate("login") {
                            popUpTo("profile") { inclusive = true }
                        }
                    },
                ) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
fun UserProfileScreen(authManager: AuthenticationManager, user: User, navController: NavController) {
    val context = LocalContext.current
    val storyRepo = StoryRepository()
    val lastStoryState = remember { mutableStateOf<Story?>(null) }
    var selectedLanguage by rememberSaveable { mutableStateOf("en") }
    val availableLanguages = listOf("en" to "English", "es" to "Español", "ja" to "日本語", "el" to "Ελληνικά")

    // Fetch last listened story
    LaunchedEffect(user.stats?.lastListenedStoryId) {
        user.stats?.lastListenedStoryId?.let { storyId ->
            val story = storyRepo.getStoryById(storyId)
            lastStoryState.value = story
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!user.profileImageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = user.profileImageUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Icon",
                        modifier = Modifier.size(100.dp)
                    )
                }
                // User Information Column
                Column {
                    Text(
                        text = user.name ?: "Your Name",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = user.email ?: "user@example.com",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Last Listened Story Card (Only show if a story is found)
        lastStoryState.value?.let { story ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("details/${story.id}") },
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.elevatedCardElevation(4.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = transformGoogleDriveUrl(story.imageUrl),
                        contentDescription = "Story Image",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(story.title, style = MaterialTheme.typography.headlineSmall)
                        Text(
                            stringResource(R.string.tap_to_continue_listening),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Inline Language Selection Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language Icon"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.languages),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                availableLanguages.forEach { (code, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedLanguage = code
                                updateLocale(context, code)
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedLanguage == code,
                            onClick = {
                                selectedLanguage = code
                                updateLocale(context, code)
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label)
                    }
                }
            }
        }

        // Logout Button
        Button(
            onClick = {
                authManager.signOut()
                navController.navigate("login") {
                    popUpTo("profile") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = stringResource(R.string.logout),
                tint = MaterialTheme.colorScheme.onError
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.logout),
                color = MaterialTheme.colorScheme.onError
            )
        }
    }
}

fun updateLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val resources = context.resources
    val config = resources.configuration
    config.setLocale(locale)
    // Update the configuration of the application context
    resources.updateConfiguration(config, resources.displayMetrics)
    // Recreate the Activity for changes to take full effect.
    if (context is Activity) {
        context.recreate()
    }
}

