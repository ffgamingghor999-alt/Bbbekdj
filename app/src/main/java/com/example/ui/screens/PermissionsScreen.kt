package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notifications.MyraNotificationListenerService
import com.example.ui.MainViewModel
import com.example.ui.components.PermissionItemCard
import com.example.ui.theme.MyraBlack
import com.example.ui.theme.MyraCyan
import com.example.ui.theme.MyraTextPrimary
import com.example.ui.theme.MyraTextSecondary

@Composable
fun PermissionsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    var isMicGranted by remember { mutableStateOf(viewModel.isMicPermissionGranted(context)) }
    var isPhoneStateGranted by remember { mutableStateOf(viewModel.isPhoneStateGranted(context)) }
    var isContactsGranted by remember { mutableStateOf(viewModel.isReadContactsGranted(context)) }

    val isAccessibilityActive by viewModel.isAccessibilityActive.collectAsStateWithLifecycle()
    val isNotificationListenerActive by viewModel.isNotificationListenerActive.collectAsStateWithLifecycle()

    val micLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isMicGranted = granted
    }

    val phoneStateLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isPhoneStateGranted = granted
    }

    val contactsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isContactsGranted = granted
    }

    val postNotifLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MyraBlack)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
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
                        text = "Android Permissions",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MyraTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Transparent native privilege management",
                        style = MaterialTheme.typography.labelSmall.copy(color = MyraTextSecondary)
                    )
                }
            }
        }

        item {
            PermissionItemCard(
                title = "Microphone Access",
                description = "Required for Speech-to-Text voice recognition in English & Bengali.",
                isGranted = isMicGranted,
                isCritical = true,
                onActionClick = { micLauncher.launch(Manifest.permission.RECORD_AUDIO) }
            )
        }

        item {
            PermissionItemCard(
                title = "Accessibility Service",
                description = "Enables MYRA to perform user-requested app navigation, clicks, and scroll gestures.",
                isGranted = isAccessibilityActive,
                isCritical = false,
                onActionClick = {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                }
            )
        }

        item {
            PermissionItemCard(
                title = "Notification Listener Access",
                description = "Allows MYRA to read and announce incoming message previews with privacy filters.",
                isGranted = isNotificationListenerActive,
                isCritical = false,
                onActionClick = {
                    context.startActivity(MyraNotificationListenerService.openSettingsIntent())
                }
            )
        }

        item {
            PermissionItemCard(
                title = "Phone State (Telephony)",
                description = "Detects incoming calls so MYRA can announce them.",
                isGranted = isPhoneStateGranted,
                isCritical = false,
                onActionClick = { phoneStateLauncher.launch(Manifest.permission.READ_PHONE_STATE) }
            )
        }

        item {
            PermissionItemCard(
                title = "Contacts Access",
                description = "Matches incoming phone numbers to your contact names for spoken announcements.",
                isGranted = isContactsGranted,
                isCritical = false,
                onActionClick = { contactsLauncher.launch(Manifest.permission.READ_CONTACTS) }
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            item {
                PermissionItemCard(
                    title = "System Notifications",
                    description = "Required to show the persistent foreground assistant notification.",
                    isGranted = true,
                    isCritical = false,
                    onActionClick = { postNotifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Privacy Guarantee: MYRA AI processes all data locally on your device by default. No audio recordings, contacts, or notification logs are ever uploaded to external servers without explicit user instruction.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MyraTextSecondary,
                    fontSize = 11.sp
                )
            )
        }
    }
}
