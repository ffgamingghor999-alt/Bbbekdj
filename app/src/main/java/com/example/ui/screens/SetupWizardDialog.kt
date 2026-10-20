package com.example.ui.screens

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.MyraCyan
import com.example.ui.theme.MyraNavyBorder
import com.example.ui.theme.MyraNavyDark
import com.example.ui.theme.MyraNavyLight
import com.example.ui.theme.MyraNavyMedium
import com.example.ui.theme.MyraNeonGreen
import com.example.ui.theme.MyraNeonViolet
import com.example.ui.theme.MyraTextMuted
import com.example.ui.theme.MyraTextOnCyan
import com.example.ui.theme.MyraTextPrimary
import com.example.ui.theme.MyraTextSecondary

@Composable
fun SetupWizardDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    var step by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    val micLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    val steps = listOf(
        WizardStep(
            title = "Welcome to MYRA AI",
            description = "Your native Android personal AI assistant. MYRA combines on-device natural language understanding, voice recognition in Bengali and English, device automation, and local memory.",
            icon = Icons.Default.AutoAwesome,
            buttonText = "GET STARTED",
            action = {}
        ),
        WizardStep(
            title = "Voice Input Permission",
            description = "Enable microphone access so MYRA can listen to your voice commands and spoken questions in real-time.",
            icon = Icons.Default.Mic,
            buttonText = "GRANT MICROPHONE",
            action = { micLauncher.launch(Manifest.permission.RECORD_AUDIO) }
        ),
        WizardStep(
            title = "Device Automation & Screen Control",
            description = "MYRA uses the Android Accessibility Service to launch apps (WhatsApp, Telegram), inspect screen nodes, and perform actions with your consent.",
            icon = Icons.Default.TouchApp,
            buttonText = "CONFIGURE ACCESSIBILITY",
            action = {
                val intent = android.content.Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }
        ),
        WizardStep(
            title = "Ready to Assist",
            description = "Setup is complete! You can customize settings, memory notes, and telephony options at any time. Tap the glowing AI Orb to begin.",
            icon = Icons.Default.CheckCircle,
            buttonText = "LAUNCH MYRA",
            action = onDismiss
        )
    )

    val currentStep = steps[step]

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MyraNavyDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MyraCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = currentStep.icon,
                        contentDescription = null,
                        tint = MyraCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = currentStep.title,
                    color = MyraTextPrimary,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column {
                Text(
                    text = currentStep.description,
                    color = MyraTextSecondary,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Dots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    steps.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (index == step) MyraCyan else MyraNavyBorder)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    currentStep.action()
                    if (step < steps.size - 1) {
                        step++
                    } else {
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MyraCyan)
            ) {
                Text(
                    text = currentStep.buttonText,
                    color = MyraTextOnCyan,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Skip Setup", color = MyraTextMuted)
            }
        }
    )
}

data class WizardStep(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val buttonText: String,
    val action: () -> Unit
)
