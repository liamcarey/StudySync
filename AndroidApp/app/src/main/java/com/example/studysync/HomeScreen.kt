package com.example.studysync

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class StudyGroup(
    val course: String,
    val title: String,
    val location: String,
    val time: String,
    val members: Int,
    val joined: Boolean = false
)

@Composable
fun HomeScreen(
    studyGroups: List<StudyGroup>,
    onCreateClick: () -> Unit,
    onJoinGroup: (Int) -> Unit
    ) {

    Scaffold(
        topBar = {
            Text(
                text = "StudySync",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(20.dp)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateClick
            ) {
                Text("+")
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Available Study Groups",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            items(studyGroups.size) { index ->
                StudyGroupCard(
                    group = studyGroups[index],
                    onJoinClick = { onJoinGroup(index) }
                )
            }
        }
    }
}

@Composable
fun StudyGroupCard(group: StudyGroup,
                   onJoinClick: () -> Unit
                    ) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = group.course,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = group.title,
                style = MaterialTheme.typography.titleMedium
            )

            Text("Location: ${group.location}")
            Text("Time: ${group.time}")
            Text("Members: ${group.members}")

            Button(
                onClick = onJoinClick,
                enabled = !group.joined,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (group.joined) "Joined" else "Join Group")
            }
        }
    }
}
