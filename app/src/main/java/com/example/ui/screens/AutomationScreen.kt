package com.example.ui.screens

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.automation.MyraAccessibilityService
import com.example.core.model.ActionType
import com.example.core.model.AutomationAction
import com.example.data.local.entity.ActionLogEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.MyraBlack
import com.example.ui.theme.MyraCyan
import com.example.ui.theme.MyraElectricBlue
import com.example.ui.theme.MyraNavyBorder
import com.example.ui.theme.MyraNavyDark
import com.example.ui.theme.MyraNavyLight
import com.example.ui.theme.MyraNavyMedium
import com.example.ui.theme.MyraNeonAmber
import com.example.ui.theme.MyraNeonGreen
import com.example.ui.theme.MyraNeonRed
import com.example.ui.theme.MyraNeonViolet
import com.example.ui.theme.MyraTextMuted
import com.example.ui.theme.MyraTextOnCyan
import com.example.ui.theme.MyraTextPrimary
import com.example.ui.theme.MyraTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AutomationScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    val isAccessibilityActive by viewModel.isAccessibilityActive.collectAsStateWithLifecycle()
    val actionLogs by viewModel.actionLogs.collectAsStateWithLifecycle()

    var pendingAction by remember { mutableStateOf<AutomationAction?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MyraBlack)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
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
                        text = "Phone Automation Engine",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MyraTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Permission-aware native Android control",
                        style = MaterialTheme.typography.labelSmall.copy(color = MyraTextSecondary)
                    )
                }
            }
        }

        // Accessibility Service Status Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAccessibilityActive) MyraNavyMedium else MyraNavyLight
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        listOf(
                            if (isAccessibilityActive) MyraNeonGreen.copy(alpha = 0.5f) else MyraNeonAmber.copy(alpha = 0.5f),
                            MyraNavyBorder
                        )
                    )
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isAccessibilityActive) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isAccessibilityActive) MyraNeonGreen else MyraNeonAmber,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isAccessibilityActive) "Accessibility Engine Active" else "Accessibility Disabled",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = MyraTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        if (!isAccessibilityActive) {
                            Button(
                                onClick = {
                                    val intent = android.content.Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                                        flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MyraCyan),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("ENABLE", color = MyraTextOnCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isAccessibilityActive) {
                            "MYRA AI has authorized accessibility access to perform user-instructed clicks, app launches, and screen scrolling."
                        } else {
                            "Enable MYRA AI in Android Settings > Accessibility to permit screen automation, app interaction, and node inspection."
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MyraTextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }

        // Global Navigation Controls
        item {
            Text(
                text = "GLOBAL ACTIONS",
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
                ActionButtonItem(
                    title = "Home",
                    icon = Icons.Default.Home,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.executeAutomation(
                            AutomationAction(type = ActionType.GLOBAL_HOME, description = "Global Home")
                        )
                    }
                )
                ActionButtonItem(
                    title = "Back",
                    icon = Icons.Default.Undo,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.executeAutomation(
                            AutomationAction(type = ActionType.GLOBAL_BACK, description = "Global Back")
                        )
                    }
                )
                ActionButtonItem(
                    title = "Recents",
                    icon = Icons.Default.Layers,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.executeAutomation(
                            AutomationAction(type = ActionType.GLOBAL_RECENTS, description = "Global Recents")
                        )
                    }
                )
                ActionButtonItem(
                    title = "Notifs",
                    icon = Icons.Default.Notifications,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.executeAutomation(
                            AutomationAction(type = ActionType.READ_NOTIFICATIONS, description = "Open Notification Shade")
                        )
                    }
                )
            }
        }

        // Screen Scrolling Controls
        item {
            Text(
                text = "ACCESSIBILITY SCROLL GESTURES",
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
                ActionButtonItem(
                    title = "Scroll Down",
                    icon = Icons.Default.ArrowDownward,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.executeAutomation(
                            AutomationAction(type = ActionType.SCROLL_DOWN, description = "Scroll down screen")
                        )
                    }
                )
                ActionButtonItem(
                    title = "Scroll Up",
                    icon = Icons.Default.ArrowUpward,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.executeAutomation(
                            AutomationAction(type = ActionType.SCROLL_UP, description = "Scroll up screen")
                        )
                    }
                )
            }
        }

        // App Automation Actions
        item {
            Text(
                text = "SUPPORTED INTEGRATIONS",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MyraCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppIntegrationCard(
                    appName = "WhatsApp",
                    packageName = "com.whatsapp",
                    description = "Draft messages and launch chat conversation",
                    onLaunch = {
                        pendingAction = AutomationAction(
                            type = ActionType.DRAFT_WHATSAPP,
                            targetApp = "com.whatsapp",
                            payload = "Hello from MYRA AI assistant!",
                            description = "Draft WhatsApp Message",
                            requiresConfirmation = true
                        )
                    }
                )

                AppIntegrationCard(
                    appName = "Telegram",
                    packageName = "org.telegram.messenger",
                    description = "Launch Telegram messenger or draft share intent",
                    onLaunch = {
                        pendingAction = AutomationAction(
                            type = ActionType.DRAFT_TELEGRAM,
                            targetApp = "org.telegram.messenger",
                            payload = "Shared from MYRA AI",
                            description = "Launch Telegram Share",
                            requiresConfirmation = true
                        )
                    }
                )

                AppIntegrationCard(
                    appName = "Android Settings",
                    packageName = "com.android.settings",
                    description = "Navigate directly to system preferences",
                    onLaunch = {
                        viewModel.executeAutomation(
                            AutomationAction(
                                type = ActionType.OPEN_SETTINGS,
                                targetApp = "com.android.settings",
                                description = "Open Device Settings"
                            )
                        )
                    }
                )
            }
        }

        // Action Audit Logs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AUDIT ACTION LOGS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MyraCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "${actionLogs.size} Events",
                    style = MaterialTheme.typography.labelSmall.copy(color = MyraTextMuted)
                )
            }
        }

        if (actionLogs.isEmpty()) {
            item {
                Text(
                    text = "No automation actions recorded yet.",
                    color = MyraTextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            items(actionLogs.take(15), key = { it.id }) { log ->
                ActionLogCard(log = log)
            }
        }
    }

    // Confirmation Dialog for Sensitive Actions
    pendingAction?.let { action ->
        AlertDialog(
            onDismissRequest = { pendingAction = null },
            containerColor = MyraNavyDark,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MyraNeonAmber
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "User Authorization Required",
                        color = MyraTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Text(
                    text = "MYRA AI is about to execute: ${action.description}. Do you authorize this action?",
                    color = MyraTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.executeAutomation(action)
                        pendingAction = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MyraCyan)
                ) {
                    Text("AUTHORIZE", color = MyraTextOnCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingAction = null }) {
                    Text("CANCEL", color = MyraTextSecondary)
                }
            }
        )
    }
}

@Composable
fun ActionButtonItem(
    title: String,
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
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MyraCyan,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MyraTextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun AppIntegrationCard(
    appName: String,
    packageName: String,
    description: String,
    onLaunch: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MyraNavyMedium)
            .border(1.dp, MyraNavyBorder, RoundedCornerShape(12.dp))
            .clickable { onLaunch() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = appName,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MyraTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MyraTextSecondary,
                    fontSize = 11.sp
                )
            )
        }
        Icon(
            imageVector = Icons.Default.Launch,
            contentDescription = "Launch",
            tint = MyraCyan,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ActionLogCard(log: ActionLogEntity) {
    val dateStr = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date(log.timestamp))
    val isSuccess = log.status == "SUCCESS"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MyraNavyMedium),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(
                    if (isSuccess) MyraNeonGreen.copy(alpha = 0.3f) else MyraNeonRed.copy(alpha = 0.3f),
                    MyraNavyBorder
                )
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isSuccess) MyraNeonGreen else MyraNeonRed)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = log.actionType,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MyraCyan,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MyraTextMuted,
                            fontSize = 9.sp
                        )
                    )
                }
                Text(
                    text = log.details,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MyraTextSecondary,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
