
package com.example.studysync

import androidx.compose.foundation.layout.*
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
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onSignUpClick: () -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val purple = Color(0xFF6849A5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text("← Back", color = purple)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Welcome Back",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Continue your study journey",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(36.dp))

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

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = {
                // Forgot password will be implemented later
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Forgot Password?", color = purple)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                onLogin(email.trim(), password)
            },
            enabled = email.isNotBlank() && password.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = purple
            )
        ) {
            Text("Log In", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(onClick = onSignUpClick) {
            Text("Don't have an account? Sign Up", color = purple)
        }
    }
}
