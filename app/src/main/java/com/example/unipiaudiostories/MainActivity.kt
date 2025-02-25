package com.example.unipiaudiostories

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable
import com.example.unipiaudiostories.data.auth.AuthenticationManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.unipiaudiostories.ui.components.AdaptiveScaffold
import com.example.unipiaudiostories.ui.screens.HomeScreen
import com.example.unipiaudiostories.ui.screens.LoginScreen
import com.example.unipiaudiostories.ui.screens.ProfileScreen
import com.example.unipiaudiostories.ui.screens.RegisterScreen
import com.example.unipiaudiostories.ui.screens.StatsScreen
import com.example.unipiaudiostories.ui.screens.StoryDetailsScreen
import com.example.unipiaudiostories.utils.LanguagePreferences
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val authManager = AuthenticationManager()

    override fun attachBaseContext(newBase: Context) {
        val languageCode = LanguagePreferences.getSelectedLanguage(newBase) ?: "en"
        val localeUpdatedContext = newBase.updateBaseContextLocale(languageCode)
        super.attachBaseContext(localeUpdatedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppContent()
        }
    }

    private fun Context.updateBaseContextLocale(languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        return createConfigurationContext(config)
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Composable
    fun AppContent() {
        val navController = rememberNavController()
        // Calculate the window size class based on the current context.
        val windowSizeClass = calculateWindowSizeClass(this)

        AdaptiveScaffold(
            navController = navController,
            windowSizeClass = windowSizeClass,
            content = {
                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = Modifier.padding(0.dp)
                ) {
                    composable("home") { HomeScreen(navController) }
                    composable("login") { LoginScreen(navController) }
                    composable("register") { RegisterScreen(navController) }
                    composable("profile") {
                        if (authManager.getCurrentUser() != null){
                            ProfileScreen(navController)
                        }else {
                            LoginScreen(navController)
                        }
                    }
                    composable(
                        "details/{storyId}",
                        arguments = listOf(navArgument("storyId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val storyId = backStackEntry.arguments?.getString("storyId")
                        storyId?.let { StoryDetailsScreen(navController, it) }
                    }
                    composable("signup") { RegisterScreen(navController) }
                    composable("stats") {
                        if (authManager.getCurrentUser() != null){
                            StatsScreen()
                        }else {
                            LoginScreen(navController)
                        }
                    }
                }
            }
        )
    }
}