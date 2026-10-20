package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.model.ActionType
import com.example.core.model.AutomationAction
import com.example.core.model.Language
import com.example.core.model.OrbState
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import com.example.ui.components.AiOrb
import com.example.ui.components.StatusHeader
import com.example.ui.theme.MyraBlack
import com.example.ui.theme.MyraCyan
import com.example.ui.theme.MyraElectricBlue
import com.example.ui.theme.MyraNavyBorder
import com.example.ui.theme.MyraNavyDark
import com.example.ui.theme.MyraNavyLight
import com.example.ui.theme.MyraNavyMedium
import com.example.ui.theme.MyraNeonGreen
import com.example.ui.theme.MyraNeonViolet
import com.example.ui.theme.MyraTextMuted
import com.example.ui.theme.MyraTextPrimary
import com.example.ui.theme.MyraTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val orbState by viewModel.orbState.collectAsStateWithLifecycle()
    val isListening by viewModel.isListening.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.isSpeaking.collectAsStateWithLifecycle()
    val audioRms by viewModel.audioRms.collectAsStateWithLifecycle()
    val speechTranscript by viewModel.speechTranscript.collectAsStateWithLifecycle()
    val recentMessages by viewModel.recentConversations.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val isServiceRunning by viewModel.isForegroundServiceRunning.collectAsStateWithLifecycle()
    val isAccessibilityActive by viewModel.isAccessibilityActive.collectAsStateWithLifecycle()
    val isNotificationActive by viewModel.isNotificationListenerActive.collectAsStateWithLifecycle()

    var quickInputText by remember { mutableStateOf("") }

    val isOnline = viewModel.isNetworkOnline(context)
    val isMicGranted = viewModel.isMicPermissionGranted(context)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(MyraBlack, MyraNavyDark, MyraBlack)
                )
            )
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Title & Tagline
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MYRA AI",
                        style = MaterialTheme.typography.displayMedium.copy(
                            color = MyraCyan,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp
                        )
                    )
                    Text(
                        text = "Native Android Personal AI Assistant",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MyraTextSecondary
                        )
                    )
                }

                // Language Quick Toggle
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
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (selectedLanguage == Language.ENGLISH) "EN" else "বাংলা",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MyraCyan,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        // Live System Status Header
        item {
            StatusHeader(
                orbState = orbState,
                isOnline = isOnline,
                isMicGranted = isMicGranted,
                isAccessibilityEnabled = isAccessibilityActive,
                isNotificationListenerEnabled = isNotificationActive,
                isServiceRunning = isServiceRunning,
                onStatusClick = { viewModel.navigateTo(ScreenDestination.DIAGNOSTICS) }
            )
        }

        // Animated AI Orb Centerpiece
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AiOrb(
                    state = orbState,
                    size = 200.dp,
                    audioRms = audioRms,
                    onClick = {
                        if (isListening) {
                            viewModel.stopListening()
                        } else if (isSpeaking) {
                            viewModel.stopSpeaking()
                        } else {
                            viewModel.startListening()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = when {
                        isListening -> "Listening... Speak now"
                        isSpeaking -> "Speaking response..."
                        orbState == OrbState.PROCESSING -> "Processing intelligence..."
                        else -> "Tap Orb or Mic to talk with MYRA"
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isListening) MyraNeonGreen else MyraCyan,
                        fontWeight = FontWeight.Medium
                    )
                )

                if (speechTranscript.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"$speechTranscript\"",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MyraTextPrimary,
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Quick Input Row (Text & Mic)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = quickInputText,
                    onValueChange = { quickInputText = it },
                    placeholder = {
                        Text(
                            text = if (selectedLanguage == Language.BENGALI) "মায়রাকে কিছু জিজ্ঞেস করুন..." else "Ask MYRA anything...",
                            color = MyraTextMuted,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_quick_input"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MyraNavyMedium,
                        unfocusedContainerColor = MyraNavyMedium,
                        focusedBorderColor = MyraCyan,
                        unfocusedBorderColor = MyraNavyBorder,
                        focusedTextColor = MyraTextPrimary,
                        unfocusedTextColor = MyraTextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (isListening) {
                            viewModel.stopListening()
                        } else {
                            viewModel.startListening()
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isListening) MyraNeonGreen else MyraNavyLight)
                        .border(1.dp, MyraCyan, CircleShape)
                        .testTag("home_mic_button")
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = "Voice input",
                        tint = if (isListening) MyraBlack else MyraCyan
                    )
                }

                if (quickInputText.isNotBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            viewModel.sendMessage(quickInputText)
                            quickInputText = ""
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MyraCyan)
                            .testTag("home_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = MyraBlack
                        )
                    }
                }
            }
        }

        // Quick Vocal Command Suggestion Chips
        item {
            Column {
                Text(
                    text = "QUICK COMMANDS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MyraCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                val samplePrompts = if (selectedLanguage == Language.BENGALI) {
                    listOf("হোয়াটসঅ্যাপ খোলো", "টেলিগ্রাম মেসেঞ্জার", "নোটিফিকেশন পড়ো", "তুমি কে?", "আজকে কি বার?")
                } else {
                    listOf("Open WhatsApp", "Open Telegram", "Read notifications", "Open settings", "Who are you?", "What time is it?")
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    samplePrompts.forEach { prompt ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(MyraNavyLight)
                                .border(1.dp, MyraNavyBorder, RoundedCornerShape(16.dp))
                                .clickable { viewModel.sendMessage(prompt) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = prompt,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MyraTextPrimary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Quick App Automation Section
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PHONE AUTOMATION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MyraCyan,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MyraElectricBlue,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.clickable {
                            viewModel.navigateTo(ScreenDestination.AUTOMATION)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AutomationQuickCard(
                        title = "WhatsApp",
                        subtitle = "Draft Chat",
                        icon = Icons.Default.Chat,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.executeAutomation(
                                AutomationAction(
                                    type = ActionType.DRAFT_WHATSAPP,
                                    targetApp = "com.whatsapp",
                                    description = "Open WhatsApp"
                                )
                            )
                        }
                    )

                    AutomationQuickCard(
                        title = "Telegram",
                        subtitle = "Launch",
                        icon = Icons.Default.Send,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.executeAutomation(
                                AutomationAction(
                                    type = ActionType.DRAFT_TELEGRAM,
                                    targetApp = "org.telegram.messenger",
                                    description = "Open Telegram"
                                )
                            )
                        }
                    )

                    AutomationQuickCard(
                        title = "Settings",
                        subtitle = "System",
                        icon = Icons.Default.Settings,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.executeAutomation(
                                AutomationAction(
                                    type = ActionType.OPEN_SETTINGS,
                                    targetApp = "com.android.settings",
                                    description = "Open Android Settings"
                                )
                            )
                        }
                    )
                }
            }
        }

        // Feature Navigation Hub Grid
        item {
            Column {
                Text(
                    text = "ASSISTANT MODULES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MyraCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ModuleButton(
                        title = "AI Chat",
                        icon = Icons.Default.Chat,
                        color = MyraCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(ScreenDestination.CHAT) }
                    )
                    ModuleButton(
                        title = "Voice Hub",
                        icon = Icons.Default.Mic,
                        color = MyraElectricBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(ScreenDestination.VOICE) }
                    )
                    ModuleButton(
                        title = "Memory",
                        icon = Icons.Default.Psychology,
                        color = MyraNeonViolet,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(ScreenDestination.MEMORY) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ModuleButton(
                        title = "Notifs",
                        icon = Icons.Default.Notifications,
                        color = MyraCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(ScreenDestination.NOTIFICATIONS) }
                    )
                    ModuleButton(
                        title = "Diagnostics",
                        icon = Icons.Default.HealthAndSafety,
                        color = MyraNeonGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(ScreenDestination.DIAGNOSTICS) }
                    )
                    ModuleButton(
                        title = "Admin",
                        icon = Icons.Default.AdminPanelSettings,
                        color = MyraNavyBorder,
                        badge = "PLANNED",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(ScreenDestination.ADMIN) }
                    )
                }
            }
        }

        // Recent Conversation Preview
        if (recentMessages.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT INTERACTIONS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MyraCyan,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Full History",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MyraElectricBlue,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.clickable {
                            viewModel.navigateTo(ScreenDestination.CHAT)
                        }
                    )
                }
            }

            items(recentMessages.take(4)) { msg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(ScreenDestination.CHAT) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (msg.isUser) MyraNavyMedium else MyraNavyLight
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            listOf(
                                if (msg.isUser) MyraNavyBorder else MyraCyan.copy(alpha = 0.3f),
                                MyraNavyBorder
                            )
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (msg.isUser) MyraElectricBlue else MyraCyan)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (msg.isUser) "You" else "MYRA AI",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (msg.isUser) MyraElectricBlue else MyraCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = msg.text,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MyraTextPrimary
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AutomationQuickCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MyraNavyMedium),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(listOf(MyraNavyBorder, MyraNavyLight))
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MyraCyan,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MyraTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MyraTextMuted,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
fun ModuleButton(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    badge: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MyraNavyMedium),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(listOf(color.copy(alpha = 0.4f), MyraNavyBorder))
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MyraTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            )
            if (badge != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MyraNeonViolet,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
