
package com.example.studysync

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Study group data model
data class StudyGroup(
    val course: String,
    val title: String,
    val location: String,
    val time: String,
    val members: Int,
    val joined: Boolean = false,
    val id: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

// Home screen
@Composable
fun HomeScreen(
    studyGroups: List<StudyGroup>,
    onCreateClick: () -> Unit,
    onMapClick: () -> Unit,
    onJoinGroup: (Int) -> Unit,
    onLogout: () -> Unit
) {

    Scaffold(
        topBar = {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 16.dp,
                        top = 36.dp,
                        bottom = 12.dp
                    ),

                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "StudySync",
                    style = MaterialTheme.typography.headlineMedium
                )

                TextButton(
                    onClick = onLogout
                ) {
                    Text(
                        text = "Log Out",
                        color = Color(0xFF6849A5)
                    )
                }
            }
        },

        floatingActionButton = {

            FloatingActionButton(
                onClick = onCreateClick,
                containerColor = Color(0xFF6849A5),
                contentColor = Color.White
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

            verticalArrangement = Arrangement.spacedBy(12.dp),

            contentPadding = PaddingValues(bottom = 88.dp)
        ) {

            // NEW: Map navigation button
            item {

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {

                    Button(
                        onClick = onMapClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6849A5)
                        )
                    ) {
                        Text("View Study Groups on Map")
                    }

                    Text(
                        text = "Available Study Groups",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            // Study group list
            itemsIndexed(studyGroups) { index, group ->

                StudyGroupCard(
                    group = group,

                    onJoinClick = {
                        onJoinGroup(index)
                    }
                )
            }
        }
    }
}

// Individual study group card
@Composable
fun StudyGroupCard(
    group: StudyGroup,
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

            Text(
                text = "Location: ${group.location}"
            )

            Text(
                text = "Time: ${group.time}"
            )

            Text(
                text = "Members: ${group.members}"
            )

            Button(
                onClick = onJoinClick,
                enabled = !group.joined,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = if (group.joined) {
                        "Joined"
                    } else {
                        "Join Group"
                    }
                )
            }
        }
    }
}
