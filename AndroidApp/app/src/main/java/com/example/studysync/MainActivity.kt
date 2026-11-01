package com.example.studysync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                StudySyncApp()
            }
        }
    }
}

@Composable
fun StudySyncApp() {

    // Login state
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showSignup by remember { mutableStateOf(false) }
    var isLoggedIn by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Navigation state
    var showCreateGroup by remember { mutableStateOf(false) }

    // Study groups retrieved from PostgreSQL
    val studyGroups = remember {
        mutableStateListOf<StudyGroup>()
    }

    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember {
        mutableStateOf<String?>(null)
    }

    // Retrieve groups when user enters the home screen
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            isLoading = true
            loadError = null

            try {
                val groups = withContext(Dispatchers.IO) {
                    StudySyncApi.getGroups()
                }

                studyGroups.clear()
                studyGroups.addAll(groups)

            } catch (e: Exception) {
                loadError = e.message ?: "Unknown error"
            } finally {
                isLoading = false
            }
        }
    }

    // Display home screen or create group screen
    if (isLoggedIn) {

        if (showCreateGroup) {
            CreateGroupScreen(
                onCreateGroup = { newGroup ->
                    scope.launch {
                        try {
                            val savedGroup = withContext(Dispatchers.IO) {
                                StudySyncApi.createGroup(newGroup)
                            }

                            studyGroups.add(0, savedGroup)
                            showCreateGroup = false

                        } catch (e: Exception) {
                            loadError = e.message ?: "Failed to create group"
                        }
                    }
                },
                onBack = {
                    showCreateGroup = false
                }
            )

        } else if (isLoading) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else if (loadError != null) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Failed to load groups: $loadError",
                    modifier = Modifier.padding(24.dp)
                )
            }

        } else {

            HomeScreen(
                studyGroups = studyGroups,

                onCreateClick = {
                    showCreateGroup = true
                },

                onJoinGroup = { index ->
                    val group = studyGroups[index]

                    if (!group.joined) {
                        studyGroups[index] = group.copy(
                            members = group.members + 1,
                            joined = true
                        )
                    }
                }
            )
        }

        return
    }

    // Login / Signup Screen
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "StudySync",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Find your next study group",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Temporary login navigation
                    // JWT authentication will be implemented later
                    isLoggedIn = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (showSignup) "Sign Up" else "Log In"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    showSignup = !showSignup
                }
            ) {
                Text(
                    if (showSignup)
                        "Already have an account? Log In"
                    else
                        "Don't have an account? Sign Up"
                )
            }
        }
    }
}
