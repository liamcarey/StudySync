package com.example.studysync

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateGroupScreen(
    onCreateGroup: (StudyGroup) -> Unit,
    onBack: () -> Unit
) {
    var course by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Create Study Group",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Organize a new study session",
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            value = course,
            onValueChange = { course = it },
            label = { Text("Course Code") },
            placeholder = { Text("e.g. SOFE 4640U") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Study Group Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            label = { Text("Date and Time") },
            placeholder = { Text("e.g. Friday, 3:00 PM") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                val newGroup = StudyGroup(
                    course = course.trim(),
                    title = title.trim(),
                    location = location.trim(),
                    time = time.trim(),
                    members = 1
                )
                onCreateGroup(newGroup)
            },
            enabled = course.isNotBlank() &&
                    title.isNotBlank() &&
                    location.isNotBlank() &&
                    time.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Group")
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}
