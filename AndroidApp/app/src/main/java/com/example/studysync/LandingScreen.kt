
package com.example.studysync

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LandingScreen(
    onSignUp: () -> Unit,
    onLogin: () -> Unit
) {
    val purple = Color(0xFF6849A5)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFF8F4FF),
                        Color(0xFFECE2FA),
                        Color(0xFFFFFFFF)
                    )
                )
            )
            .padding(28.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            // Branding
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .background(
                            purple,
                            RoundedCornerShape(26.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "S",
                        color = Color.White,
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    "StudySync",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF302344)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Find your people.\nStudy better together.",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF302344),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    "Discover nearby study groups, connect with classmates and make every study session count.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF70677D),
                    textAlign = TextAlign.Center
                )
            }

            // Features
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                FeatureItem("📚", "Find study groups")
                FeatureItem("📍", "Discover nearby sessions")
                FeatureItem("🤝", "Connect with classmates")
            }

            // Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onSignUp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = purple
                    )
                ) {
                    Text(
                        "Create Account",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = onLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Log In",
                        color = purple,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    "Made for students, built for collaboration.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF81758F)
                )
            }
        }
    }
}

@Composable
fun FeatureItem(
    emoji: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            emoji,
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            description,
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF51475E)
        )
    }
}
