
package com.example.studysync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AppScreen {
    LANDING,
    SIGNUP,
    LOGIN,
    HOME,
    MAP
}

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

    var currentScreen by remember {
        mutableStateOf(AppScreen.LANDING)
    }

    val scope = rememberCoroutineScope()

    val studyGroups = remember {
        mutableStateListOf<StudyGroup>()
    }

    var showCreateGroup by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var loadError by remember {
        mutableStateOf<String?>(null)
    }

    var refreshVersion by remember {
        mutableIntStateOf(0)
    }

    // Retrieve study groups from PostgreSQL
    LaunchedEffect(currentScreen, refreshVersion) {

        if (currentScreen == AppScreen.HOME) {

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

    when (currentScreen) {

        // LANDING SCREEN
        AppScreen.LANDING -> {

            LandingScreen(
                onSignUp = {
                    currentScreen = AppScreen.SIGNUP
                },

                onLogin = {
                    currentScreen = AppScreen.LOGIN
                }
            )
        }

        // SIGNUP SCREEN
        AppScreen.SIGNUP -> {

            SignUpScreen(
                onSignUp = { first, last, email, password ->

                    // Existing demo navigation
                    currentScreen = AppScreen.HOME
                },

                onLoginClick = {
                    currentScreen = AppScreen.LOGIN
                },

                onBack = {
                    currentScreen = AppScreen.LANDING
                }
            )
        }

        // LOGIN SCREEN
        AppScreen.LOGIN -> {

            LoginScreen(
                onLogin = { email, password ->

                    // Existing demo navigation
                    currentScreen = AppScreen.HOME
                },

                onSignUpClick = {
                    currentScreen = AppScreen.SIGNUP
                },

                onBack = {
                    currentScreen = AppScreen.LANDING
                }
            )
        }

        // HOME SCREEN
        AppScreen.HOME -> {

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
                                loadError = null

                            } catch (e: Exception) {

                                loadError = e.message
                                    ?: "Failed to create group"
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

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),

                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "Failed to load groups: $loadError"
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Button(
                        onClick = {
                            refreshVersion++
                        }
                    ) {
                        Text("Retry")
                    }
                }

            } else {

                HomeScreen(
                    studyGroups = studyGroups,

                    onCreateClick = {
                        showCreateGroup = true
                    },

                    // NEW: Open Google Maps
                    onMapClick = {
                        currentScreen = AppScreen.MAP
                    },

                    onJoinGroup = { index ->

                        if (index in studyGroups.indices) {

                            val group = studyGroups[index]

                            if (!group.joined) {

                                studyGroups[index] = group.copy(
                                    members = group.members + 1,
                                    joined = true
                                )
                            }
                        }
                    },

                    onLogout = {

                        studyGroups.clear()
                        showCreateGroup = false
                        loadError = null

                        currentScreen = AppScreen.LANDING
                    }
                )
            }
        }

        // GOOGLE MAPS SCREEN
        AppScreen.MAP -> {

            StudyMapScreen(
                studyGroups = studyGroups,

                onBack = {
                    currentScreen = AppScreen.HOME
                }
            )
        }
    }
}
