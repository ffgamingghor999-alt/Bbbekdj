package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun AdminScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var mockPairingCode by remember { mutableStateOf("MYRA-8942-X7K") }

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Admin Panel",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MyraTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MyraNeonAmber.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "PLANNED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MyraNeonAmber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                    Text(
                        text = "Secondary device control & remote management architecture",
                        style = MaterialTheme.typography.labelSmall.copy(color = MyraTextSecondary)
                    )
                }
            }
        }

        // Security Notice Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MyraNavyMedium),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(MyraNeonAmber.copy(alpha = 0.4f), MyraNavyBorder))
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MyraNeonAmber,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Zero-Trust Remote Security Architecture",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = MyraTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "In compliance with strict Android security policies, an unauthenticated or unprotected remote server is NEVER opened. Remote administration requires end-to-end cryptographic pairing, mutual TLS, and explicit on-device authorization.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MyraTextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }
        }

        // Planned Pairing Workflow
        item {
            Text(
                text = "PAIRING SPECIFICATION (EXPIRING TOKENS)",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MyraCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MyraNavyMedium),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(MyraNavyBorder, MyraNavyLight))
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        tint = MyraCyan,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Single-Use One-Time Pairing Code",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = MyraTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MyraNavyLight)
                            .border(1.dp, MyraCyan, RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = mockPairingCode,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MyraCyan,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Expires in 5 minutes • Requires physical biometric authorization on primary phone",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MyraTextMuted,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }

        // Technical Road Map
        item {
            Text(
                text = "FUTURE ROADMAP CAPABILITIES",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MyraCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminFeatureItem(
                    title = "Mutual TLS / WebRTC Control Channel",
                    description = "End-to-end encrypted peer-to-peer link between your paired devices"
                )
                AdminFeatureItem(
                    title = "Remote Telemetry & Battery Monitor",
                    description = "Live battery percentage, network state, and service diagnostics"
                )
                AdminFeatureItem(
                    title = "Remote Automation Triggers",
                    description = "Initiate pre-authorized automated tasks with push notification challenge"
                )
                AdminFeatureItem(
                    title = "Cryptographic Audit Ledger",
                    description = "Tamper-proof record of all remote commands and physical device approvals"
                )
            }
        }
    }
}

@Composable
fun AdminFeatureItem(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MyraNavyMedium),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(MyraNavyBorder, MyraNavyLight))
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = MyraNeonViolet,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MyraTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
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
        }
    }
}
