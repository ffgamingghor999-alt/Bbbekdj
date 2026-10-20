package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AutomationScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.DiagnosticsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.NotificationScreen
import com.example.ui.screens.PermissionsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SetupWizardDialog
import com.example.ui.screens.VoiceAssistantScreen
import com.example.ui.theme.MYRAAITheme
import com.example.ui.theme.MyraBlack
import com.example.ui.theme.MyraCyan
import com.example.ui.theme.MyraNavyBorder
import com.example.ui.theme.MyraNavyDark
import com.example.ui.theme.MyraNavyMedium
import com.example.ui.theme.MyraTextMuted
import com.example.ui.theme.MyraTextPrimary

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MYRAAITheme {
                MyraMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MyraMainApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val isFirstLaunch by viewModel.isFirstLaunch.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MyraBlack,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Only show bottom navigation on primary screens
            if (currentScreen in listOf(
                    ScreenDestination.HOME,
                    ScreenDestination.CHAT,
                    ScreenDestination.VOICE,
                    ScreenDestination.MEMORY,
                    ScreenDestination.SETTINGS
                )
            ) {
                MyraBottomBar(
                    currentScreen = currentScreen,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    ScreenDestination.HOME -> HomeScreen(viewModel = viewModel)
                    ScreenDestination.CHAT -> ChatScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                    ScreenDestination.VOICE -> VoiceAssistantScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                    ScreenDestination.MEMORY -> MemoryScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                    ScreenDestination.AUTOMATION -> AutomationScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                    ScreenDestination.NOTIFICATIONS -> NotificationScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                    ScreenDestination.SETTINGS -> SettingsScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                    ScreenDestination.PERMISSIONS -> PermissionsScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                    ScreenDestination.DIAGNOSTICS -> DiagnosticsScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                    ScreenDestination.ADMIN -> AdminScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }
            }

            // Onboarding Setup Wizard Dialog
            if (isFirstLaunch) {
                SetupWizardDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.dismissFirstLaunch() }
                )
            }
        }
    }
}

@Composable
fun MyraBottomBar(
    currentScreen: ScreenDestination,
    onNavigate: (ScreenDestination) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .background(MyraNavyDark)
            .testTag("myra_bottom_navigation"),
        containerColor = MyraNavyDark,
        tonalElevation = 8.dp
    ) {
        val navItems = listOf(
            NavItem(ScreenDestination.HOME, "Home", Icons.Default.Home),
            NavItem(ScreenDestination.CHAT, "Chat", Icons.Default.Chat),
            NavItem(ScreenDestination.VOICE, "Voice", Icons.Default.Mic),
            NavItem(ScreenDestination.MEMORY, "Memory", Icons.Default.Psychology),
            NavItem(ScreenDestination.SETTINGS, "Settings", Icons.Default.Settings)
        )

        navItems.forEach { item ->
            val isSelected = currentScreen == item.destination
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.destination) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) MyraCyan else MyraTextMuted
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (isSelected) MyraCyan else MyraTextMuted,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MyraNavyMedium,
                    selectedIconColor = MyraCyan,
                    unselectedIconColor = MyraTextMuted,
                    selectedTextColor = MyraCyan,
                    unselectedTextColor = MyraTextMuted
                )
            )
        }
    }
}

data class NavItem(
    val destination: ScreenDestination,
    val label: String,
    val icon: ImageVector
)
