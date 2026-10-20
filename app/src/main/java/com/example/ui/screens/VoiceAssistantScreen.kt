package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.model.Language
import com.example.core.model.OrbState
import com.example.ui.MainViewModel
import com.example.ui.components.AiOrb
import com.example.ui.theme.MyraBlack
import com.example.ui.theme.MyraCyan
import com.example.ui.theme.MyraNavyBorder
import com.example.ui.theme.MyraNavyDark
import com.example.ui.theme.MyraNavyLight
import com.example.ui.theme.MyraNavyMedium
import com.example.ui.theme.MyraNeonGreen
import com.example.ui.theme.MyraTextMuted
import com.example.ui.theme.MyraTextPrimary
import com.example.ui.theme.MyraTextSecondary

@Composable
fun VoiceAssistantScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val orbState by viewModel.orbState.collectAsStateWithLifecycle()
    val isListening by viewModel.isListening.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.isSpeaking.collectAsStateWithLifecycle()
    val audioRms by viewModel.audioRms.collectAsStateWithLifecycle()
    val speechTranscript by viewModel.speechTranscript.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val voiceError by viewModel.voiceError.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(MyraNavyDark, MyraBlack)
                )
            )
            .navigationBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Navigation & Language Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MyraCyan
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MyraNavyMedium)
                    .border(1.dp, MyraCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .clickable {
                        viewModel.setLanguage(
                            if (selectedLanguage == Language.ENGLISH) Language.BENGALI else Language.ENGLISH
                        )
                    }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (selectedLanguage == Language.ENGLISH) "🌐 English (US)" else "🌐 বাংলা (BD)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MyraCyan,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.5f))

        // Large Animated AI Orb Centerpiece
        AiOrb(
            state = orbState,
            size = 240.dp,
            audioRms = audioRms,
            onClick = {
                if (isListening) viewModel.stopListening() else viewModel.startListening()
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // State indicator text
        Text(
            text = when {
                isListening -> "LISTENING TO YOUR VOICE..."
                isSpeaking -> "MYRA IS SPEAKING..."
                orbState == OrbState.PROCESSING -> "PROCESSING INTELLIGENCE..."
                else -> "READY • TAP MIC TO SPEAK"
            },
            style = MaterialTheme.typography.titleMedium.copy(
                color = if (isListening) MyraNeonGreen else MyraCyan,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Real-time transcript display box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MyraNavyMedium.copy(alpha = 0.7f))
                .border(1.dp, MyraNavyBorder, RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when {
                    speechTranscript.isNotBlank() -> "\"$speechTranscript\""
                    isListening -> "Listening to speech audio..."
                    else -> if (selectedLanguage == Language.BENGALI) "কথা বলতে নিচের মাইকে স্পর্শ করুন..." else "Tap the microphone below to begin..."
                },
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = if (speechTranscript.isNotBlank()) MyraTextPrimary else MyraTextMuted,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                ),
                minLines = 2
            )
        }

        AnimatedVisibility(visible = voiceError != null) {
            voiceError?.let { err ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = err,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Big Voice Action Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSpeaking) {
                IconButton(
                    onClick = { viewModel.stopSpeaking() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MyraNavyLight)
                        .border(1.dp, MyraNavyBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Mute",
                        tint = MyraCyan
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
            }

            // Big Mic Action Button
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        if (isListening) MyraNeonGreen else MyraCyan
                    )
                    .clickable {
                        if (isListening) viewModel.stopListening() else viewModel.startListening()
                    }
                    .testTag("voice_assistant_mic_fab"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = "Voice Assistant Button",
                    tint = MyraBlack,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (selectedLanguage == Language.BENGALI) "বাংলায় কথা বলুন • উদাহরণ: \"হোয়াটসঅ্যাপ খোলো\"" else "Speak naturally • Try: \"Open WhatsApp\" or \"Read notifications\"",
            style = MaterialTheme.typography.labelSmall.copy(
                color = MyraTextSecondary,
                textAlign = TextAlign.Center
            )
        )
    }
}
