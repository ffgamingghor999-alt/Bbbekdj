package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.OrbState
import com.example.ui.theme.MyraCyan
import com.example.ui.theme.MyraNavyBorder
import com.example.ui.theme.MyraNavyLight
import com.example.ui.theme.MyraNavyMedium
import com.example.ui.theme.MyraNeonAmber
import com.example.ui.theme.MyraNeonGreen
import com.example.ui.theme.MyraNeonRed
import com.example.ui.theme.MyraNeonViolet
import com.example.ui.theme.MyraTextMuted
import com.example.ui.theme.MyraTextPrimary
import com.example.ui.theme.MyraTextSecondary

@Composable
fun StatusHeader(
    orbState: OrbState,
    isOnline: Boolean,
    isMicGranted: Boolean,
    isAccessibilityEnabled: Boolean,
    isNotificationListenerEnabled: Boolean,
    isServiceRunning: Boolean,
    onStatusClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MyraNavyMedium)
            .border(1.dp, MyraNavyBorder, RoundedCornerShape(16.dp))
            .clickable { onStatusClick() }
            .padding(12.dp)
            .testTag("status_header_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            when (orbState) {
                                OrbState.IDLE -> MyraCyan
                                OrbState.LISTENING -> MyraNeonGreen
                                OrbState.PROCESSING -> MyraNeonViolet
                                OrbState.SPEAKING -> MyraCyan
                                OrbState.OFFLINE -> MyraNeonAmber
                                OrbState.ERROR -> MyraNeonRed
                            }
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MYRA SYSTEM: ${orbState.name}",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MyraTextPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }

            Text(
                text = if (isServiceRunning) "BG SERVICE ON" else "STANDBY",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isServiceRunning) MyraNeonGreen else MyraTextMuted,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Indicator pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            StatusPill(
                icon = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                label = if (isOnline) "Net" else "Local",
                isActive = isOnline,
                activeColor = MyraCyan,
                modifier = Modifier.weight(1f)
            )
            StatusPill(
                icon = if (isMicGranted) Icons.Default.Mic else Icons.Default.MicOff,
                label = "Mic",
                isActive = isMicGranted,
                activeColor = MyraNeonGreen,
                modifier = Modifier.weight(1f)
            )
            StatusPill(
                icon = Icons.Default.Bolt,
                label = "Auto",
                isActive = isAccessibilityEnabled,
                activeColor = MyraNeonViolet,
                modifier = Modifier.weight(1f)
            )
            StatusPill(
                icon = Icons.Default.Notifications,
                label = "Notif",
                isActive = isNotificationListenerEnabled,
                activeColor = MyraCyan,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatusPill(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isActive) activeColor.copy(alpha = 0.15f) else MyraNavyLight.copy(alpha = 0.5f))
            .border(
                1.dp,
                if (isActive) activeColor.copy(alpha = 0.5f) else MyraNavyBorder.copy(alpha = 0.4f),
                RoundedCornerShape(8.dp)
            )
            .padding(vertical = 6.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = if (isActive) activeColor else MyraTextMuted
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isActive) activeColor else MyraTextMuted
                )
            )
        }
    }
}

@Composable
fun PermissionItemCard(
    title: String,
    description: String,
    isGranted: Boolean,
    isCritical: Boolean,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MyraNavyMedium)
            .border(1.dp, if (isGranted) MyraNeonGreen.copy(alpha = 0.3f) else MyraNavyBorder, RoundedCornerShape(12.dp))
            .clickable { onActionClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = if (isGranted) MyraNeonGreen else if (isCritical) MyraNeonAmber else MyraTextMuted,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MyraTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MyraTextSecondary,
                    fontSize = 12.sp
                )
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isGranted) MyraNeonGreen.copy(alpha = 0.15f) else MyraCyan.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (isGranted) "ENABLED" else "GRANT",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isGranted) MyraNeonGreen else MyraCyan,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
