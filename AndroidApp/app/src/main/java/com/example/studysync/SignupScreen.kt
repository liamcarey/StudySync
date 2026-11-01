
package com.example.studysync

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun SignUpScreen(
    onSignUp: (String, String, String, String) -> Unit,
    onLoginClick: () -> Unit,
    onBack: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val purple = Color(0xFF6849A5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                start = 24.dp,
                end = 24.dp,
                top = 48.dp,
                bottom = 24.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(
            onClick = {
                android.util.Log.d("StudySync", "Back button clicked")
                onBack()
            },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text("← Back", color = purple)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Join your campus study community",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(28.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("University Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password (minimum 8 characters)") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        errorMessage?.let {
            Spacer(modifier = Modifier.height(10.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                when {
                    firstName.isBlank() || lastName.isBlank() ||
                            email.isBlank() || password.isBlank() -> {
                        errorMessage = "Please complete all fields"
                    }
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> {
                        errorMessage = "Enter a valid email address"
                    }
                    password.length < 8 -> {
                        errorMessage = "Password must be at least 8 characters"
                    }
                    password != confirmPassword -> {
                        errorMessage = "Passwords do not match"
                    }
                    else -> {
                        errorMessage = null
                        onSignUp(
                            firstName.trim(),
                            lastName.trim(),
                            email.trim(),
                            password
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = purple
            )
        ) {
            Text("Create Account", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(18.dp))

        TextButton(onClick = onLoginClick) {
            Text("Already have an account? Log In", color = purple)
        }
    }
}
