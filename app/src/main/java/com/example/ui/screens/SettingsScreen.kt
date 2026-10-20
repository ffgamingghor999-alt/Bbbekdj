package com.example.ui.screens

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.model.AiProviderType
import com.example.core.model.Language
import com.example.ui.MainViewModel
import com.example.ui.theme.MyraBlack
import com.example.ui.theme.MyraCyan
import com.example.ui.theme.MyraNavyBorder
import com.example.ui.theme.MyraNavyDark
import com.example.ui.theme.MyraNavyLight
import com.example.ui.theme.MyraNavyMedium
import com.example.ui.theme.MyraNeonAmber
import com.example.ui.theme.MyraNeonViolet
import com.example.ui.theme.MyraTextMuted
import com.example.ui.theme.MyraTextOnCyan
import com.example.ui.theme.MyraTextPrimary
import com.example.ui.theme.MyraTextSecondary

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val selectedAiProvider by viewModel.selectedAiProvider.collectAsStateWithLifecycle()
    val apiKey by viewModel.onlineApiKey.collectAsStateWithLifecycle()
    val callAnnouncementsEnabled by viewModel.callAnnouncementsEnabled.collectAsStateWithLifecycle()
    val isServiceRunning by viewModel.isForegroundServiceRunning.collectAsStateWithLifecycle()

    var apiKeyInput by remember(apiKey) { mutableStateOf(apiKey) }
    var isKeyVisible by remember { mutableStateOf(false) }

    var speechRate by remember { mutableFloatStateOf(1.0f) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MyraBlack)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MyraCyan
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Settings & AI Engine",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MyraTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Configure AI provider, voice, and background tasks",
                        style = MaterialTheme.typography.labelSmall.copy(color = MyraTextSecondary)
                    )
                }
            }
        }

        // Language Selection
        item {
            SettingsSectionHeader(title = "LANGUAGE SELECTION", icon = Icons.Default.Language)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LanguageSelectionCard(
                    title = "English (US)",
                    subtitle = "Default system voice",
                    isSelected = selectedLanguage == Language.ENGLISH,
                    modifier = Modifier.weight(1f),
                    onSelect = { viewModel.setLanguage(Language.ENGLISH) }
                )
                LanguageSelectionCard(
                    title = "বাংলা (Bengali)",
                    subtitle = "বাংলা ভয়েস ও ইন্টারফেস",
                    isSelected = selectedLanguage == Language.BENGALI,
                    modifier = Modifier.weight(1f),
                    onSelect = { viewModel.setLanguage(Language.BENGALI) }
                )
            }
        }

        // AI Provider Engine Selection
        item {
            SettingsSectionHeader(title = "AI INFERENCE ENGINE", icon = Icons.Default.Psychology)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AiProviderType.entries.forEach { provider ->
                    AiProviderCard(
                        provider = provider,
                        isSelected = selectedAiProvider == provider,
                        onSelect = { viewModel.setAiProvider(provider) }
                    )
                }
            }
        }

        // API Key Configuration (if online AI selected)
        if (selectedAiProvider.requiresKey) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MyraNavyMedium),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(MyraNavyBorder, MyraNavyLight))
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = MyraCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${selectedAiProvider.displayName} API Key",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = MyraTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = apiKeyInput,
                            onValueChange = {
                                apiKeyInput = it
                                viewModel.setApiKey(it)
                            },
                            placeholder = { Text("Enter your API Key...", color = MyraTextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { isKeyVisible = !isKeyVisible }) {
                                    Icon(
                                        imageVector = if (isKeyVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle visibility",
                                        tint = MyraTextMuted
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MyraTextPrimary,
                                unfocusedTextColor = MyraTextPrimary,
                                focusedBorderColor = MyraCyan,
                                unfocusedBorderColor = MyraNavyBorder
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Stored locally on device. Never hardcoded or shared. Falls back seamlessly to offline engine when disconnected.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MyraTextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Voice TTS Adjustments
        item {
            SettingsSectionHeader(title = "SPEECH SYNTHESIS (TTS)", icon = Icons.Default.RecordVoiceOver)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MyraNavyMedium),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(MyraNavyBorder, MyraNavyLight))
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Speech Rate: ${"%.1f".format(speechRate)}x",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MyraTextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Slider(
                        value = speechRate,
                        onValueChange = { speechRate = it },
                        valueRange = 0.5f..2.0f,
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = MyraCyan,
                            activeTrackColor = MyraCyan,
                            inactiveTrackColor = MyraNavyBorder
                        )
                    )

                    Button(
                        onClick = {
                            viewModel.speakText(
                                if (selectedLanguage == Language.BENGALI) "এটি মায়রা এআই এর ভয়েস পরীক্ষার বার্তা।" else "This is a test of MYRA AI voice playback."
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MyraNavyLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Test Voice Playback", color = MyraCyan)
                    }
                }
            }
        }

        // Telephony & Call Announcement Switch
        item {
            SettingsSectionHeader(title = "INCOMING CALL ANNOUNCEMENTS", icon = Icons.Default.PhoneInTalk)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MyraNavyMedium),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(MyraNavyBorder, MyraNavyLight))
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Announce Incoming Callers",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = MyraTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Uses TextToSpeech to announce caller name from contacts or phone number.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MyraTextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Switch(
                        checked = callAnnouncementsEnabled,
                        onCheckedChange = { viewModel.toggleCallAnnouncements(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MyraCyan,
                            checkedTrackColor = MyraNavyLight,
                            uncheckedThumbColor = MyraTextMuted,
                            uncheckedTrackColor = MyraNavyDark
                        )
                    )
                }
            }
        }

        // Background Foreground Service Switch
        item {
            SettingsSectionHeader(title = "BACKGROUND PERSISTENCE", icon = Icons.Default.Schedule)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MyraNavyMedium),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(MyraNavyBorder, MyraNavyLight))
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Foreground Assistant Service",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = MyraTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Keeps MYRA active in background with ongoing system notification.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MyraTextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Switch(
                        checked = isServiceRunning,
                        onCheckedChange = { viewModel.toggleForegroundService(context) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MyraCyan,
                            checkedTrackColor = MyraNavyLight,
                            uncheckedThumbColor = MyraTextMuted,
                            uncheckedTrackColor = MyraNavyDark
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 6.dp)) {
        Icon(imageVector = icon, contentDescription = null, tint = MyraCyan, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MyraCyan,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )
    }
}

@Composable
fun LanguageSelectionCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MyraCyan.copy(alpha = 0.15f) else MyraNavyMedium)
            .border(
                1.dp,
                if (isSelected) MyraCyan else MyraNavyBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable { onSelect() }
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = if (isSelected) MyraCyan else MyraTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MyraTextSecondary,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
fun AiProviderCard(
    provider: AiProviderType,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MyraCyan.copy(alpha = 0.12f) else MyraNavyMedium)
            .border(
                1.dp,
                if (isSelected) MyraCyan else MyraNavyBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable { onSelect() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(if (isSelected) MyraCyan else Color.Transparent)
                .border(2.dp, if (isSelected) MyraCyan else MyraTextMuted, androidx.compose.foundation.shape.CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = provider.displayName,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = if (isSelected) MyraCyan else MyraTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = if (provider == AiProviderType.LOCAL_OFFLINE) "100% on-device local intent engine • No internet or API key required" else "Requires internet connection and user-provided API key",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MyraTextSecondary,
                    fontSize = 11.sp
                )
            )
        }
    }
}
