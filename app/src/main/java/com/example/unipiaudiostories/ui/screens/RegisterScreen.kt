package com.example.unipiaudiostories.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.unipiaudiostories.R
import com.example.unipiaudiostories.viewmodel.RegisterViewModel

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@SuppressLint("ContextCastToActivity")
@Composable
fun RegisterScreen(navController: NavHostController) {
    val viewModel: RegisterViewModel = viewModel()
    val context = LocalContext.current as ComponentActivity
    val windowSizeClass = calculateWindowSizeClass(context)
    val formWidthFraction = if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) 0.5f else 1f

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(formWidthFraction)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Register",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            val username = remember { mutableStateOf("") }
            val email = remember { mutableStateOf("") }
            val password = remember { mutableStateOf("") }
            val confirmPassword = remember { mutableStateOf("") }

            // Username Input
            TextField(
                value = username.value,
                onValueChange = { username.value = it },
                label = { Text(stringResource(R.string.username)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Email Input
            TextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text(stringResource(R.string.email)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Password Input
            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text(stringResource(R.string.password)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Confirm Password Input
            TextField(
                value = confirmPassword.value,
                onValueChange = { confirmPassword.value = it },
                label = { Text(stringResource(R.string.confirm_password)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (email.value.isBlank() || password.value.isBlank() || confirmPassword.value.isBlank()) {
                        Toast.makeText(context, "All fields are required.", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    if (password.value != confirmPassword.value) {
                        Toast.makeText(context, "Passwords do not match.", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    if (password.value.length < 6) {
                        Toast.makeText(context, "Password must be at least 6 characters.", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    viewModel.register(username.value, email.value, password.value, {
                        navController.navigate("profile") {
                            popUpTo("signup") { inclusive = true }
                        }}
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = { navController.navigate("login") }
            ) {
                Text(stringResource(R.string.has_account) + stringResource(R.string.login) )
            }
        }
    }
}