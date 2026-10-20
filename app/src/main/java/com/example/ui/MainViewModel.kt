package com.example.ui

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MyraApplication
import com.example.automation.AutomationManager
import com.example.automation.AutomationResult
import com.example.automation.MyraAccessibilityService
import com.example.core.model.ActionType
import com.example.core.model.AiProviderType
import com.example.core.model.AutomationAction
import com.example.core.model.Language
import com.example.core.model.NotificationItem
import com.example.core.model.OrbState
import com.example.data.local.entity.ActionLogEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.MemoryNoteEntity
import com.example.notifications.MyraNotificationListenerService
import com.example.service.MyraForegroundService
import com.example.telephony.CallAnnouncementSettings
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenDestination {
    HOME,
    CHAT,
    VOICE,
    MEMORY,
    AUTOMATION,
    NOTIFICATIONS,
    SETTINGS,
    PERMISSIONS,
    DIAGNOSTICS,
    ADMIN
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val myraApp: MyraApplication = application as MyraApplication
    private val repository = myraApp.repository
    private val aiBrain = myraApp.aiBrain
    private val voiceManager = myraApp.voiceManager
    private val automationManager: AutomationManager = myraApp.automationManager

    // Navigation Stack
    private val _currentScreen = MutableStateFlow(ScreenDestination.HOME)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    // Orb State
    private val _orbState = MutableStateFlow(OrbState.IDLE)
    val orbState: StateFlow<OrbState> = _orbState.asStateFlow()

    // Settings
    private val _selectedLanguage = MutableStateFlow(Language.ENGLISH)
    val selectedLanguage: StateFlow<Language> = _selectedLanguage.asStateFlow()

    private val _selectedAiProvider = MutableStateFlow(AiProviderType.LOCAL_OFFLINE)
    val selectedAiProvider: StateFlow<AiProviderType> = _selectedAiProvider.asStateFlow()

    private val _onlineApiKey = MutableStateFlow("")
    val onlineApiKey: StateFlow<String> = _onlineApiKey.asStateFlow()

    private val _isFirstLaunch = MutableStateFlow(false)
    val isFirstLaunch: StateFlow<Boolean> = _isFirstLaunch.asStateFlow()

    private val _callAnnouncementsEnabled = MutableStateFlow(false)
    val callAnnouncementsEnabled: StateFlow<Boolean> = _callAnnouncementsEnabled.asStateFlow()

    // UI Snackbars / Notifications
    private val _uiEvents = MutableSharedFlow<String>()
    val uiEvents: SharedFlow<String> = _uiEvents.asSharedFlow()

    // Live Database Streams
    val conversations: StateFlow<List<ConversationEntity>> = repository.allConversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentConversations: StateFlow<List<ConversationEntity>> = repository.getRecentConversations(6)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memoryNotes: StateFlow<List<MemoryNoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val actionLogs: StateFlow<List<ActionLogEntity>> = repository.allActionLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Voice Streams
    val isListening: StateFlow<Boolean> = voiceManager.isListening
    val isSpeaking: StateFlow<Boolean> = voiceManager.isSpeaking
    val speechTranscript: StateFlow<String> = voiceManager.speechTranscript
    val audioRms: StateFlow<Float> = voiceManager.audioRmsDb
    val voiceError: StateFlow<String?> = voiceManager.errorMessage

    // Notifications Stream
    val capturedNotifications: StateFlow<List<NotificationItem>> =
        MyraNotificationListenerService.capturedNotifications

    // Services & Automation State
    val isAccessibilityActive: StateFlow<Boolean> = MyraAccessibilityService.isServiceActive
    val isForegroundServiceRunning: StateFlow<Boolean> = MyraForegroundService.isServiceRunning
    val isNotificationListenerActive: StateFlow<Boolean> = MyraNotificationListenerService.isListenerConnected

    init {
        // Wire voice recognizer output to AI brain
        voiceManager.onSpeechRecognized = { transcript ->
            handleVoiceInput(transcript)
        }

        // Check if database has any messages, if not trigger onboarding check
        viewModelScope.launch {
            val count = repository.getConversationCount()
            if (count == 0) {
                _isFirstLaunch.value = true
                // Add initial MYRA welcome message
                repository.saveMessage(
                    text = "Welcome to MYRA AI! I am your native Android personal assistant. Tap the glowing orb or mic to speak in English or বাংলা.",
                    isUser = false,
                    languageCode = "en"
                )
            }
        }
    }

    fun navigateTo(screen: ScreenDestination) {
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (_currentScreen.value != ScreenDestination.HOME) {
            _currentScreen.value = ScreenDestination.HOME
            return true
        }
        return false
    }

    fun dismissFirstLaunch() {
        _isFirstLaunch.value = false
    }

    fun setLanguage(lang: Language) {
        _selectedLanguage.value = lang
    }

    fun setAiProvider(provider: AiProviderType) {
        _selectedAiProvider.value = provider
    }

    fun setApiKey(key: String) {
        _onlineApiKey.value = key
    }

    fun toggleCallAnnouncements(enabled: Boolean) {
        _callAnnouncementsEnabled.value = enabled
        CallAnnouncementSettings.isEnabled = enabled
    }

    fun toggleForegroundService(context: Context) {
        if (isForegroundServiceRunning.value) {
            MyraForegroundService.stopService(context)
            emitEvent("Foreground service stopped")
        } else {
            MyraForegroundService.startService(context)
            emitEvent("Foreground service started")
        }
    }

    fun startListening() {
        _orbState.value = OrbState.LISTENING
        voiceManager.startListening(_selectedLanguage.value)
    }

    fun stopListening() {
        voiceManager.stopListening()
        if (_orbState.value == OrbState.LISTENING) {
            _orbState.value = OrbState.IDLE
        }
    }

    private fun handleVoiceInput(transcript: String) {
        sendMessage(transcript)
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        viewModelScope.launch {
            _orbState.value = OrbState.PROCESSING
            // 1. Save user message to database
            repository.saveMessage(
                text = userText,
                isUser = true,
                languageCode = _selectedLanguage.value.code
            )

            // 2. Query AI Brain
            val response = aiBrain.processUserPrompt(
                prompt = userText,
                provider = _selectedAiProvider.value,
                apiKey = _onlineApiKey.value,
                language = _selectedLanguage.value
            )

            // 3. Save AI response
            repository.saveMessage(
                text = response.replyText,
                isUser = false,
                languageCode = _selectedLanguage.value.code,
                suggestedAction = response.suggestedAction?.type?.name
            )

            // 4. Update Orb & Speak AI response aloud
            _orbState.value = OrbState.SPEAKING
            voiceManager.speak(response.replyText, _selectedLanguage.value) {
                _orbState.value = OrbState.IDLE
            }

            // 5. Automatically propose or execute action if recognized
            response.suggestedAction?.let { action ->
                if (!action.requiresConfirmation) {
                    executeAutomation(action)
                }
            }
        }
    }

    fun executeAutomation(action: AutomationAction) {
        viewModelScope.launch {
            val res: AutomationResult = automationManager.executeAction(action)
            emitEvent(res.message)
        }
    }

    fun speakText(text: String) {
        _orbState.value = OrbState.SPEAKING
        voiceManager.speak(text, _selectedLanguage.value) {
            _orbState.value = OrbState.IDLE
        }
    }

    fun stopSpeaking() {
        voiceManager.stopSpeaking()
        _orbState.value = OrbState.IDLE
    }

    // Memory operations
    fun addMemoryNote(title: String, content: String, category: String = "General") {
        viewModelScope.launch {
            repository.saveNote(title, content, category)
            emitEvent("Memory note saved")
        }
    }

    fun deleteMemoryNote(id: Long) {
        viewModelScope.launch {
            repository.deleteNote(id)
            emitEvent("Memory note deleted")
        }
    }

    fun clearAllMemory() {
        viewModelScope.launch {
            repository.clearAllNotes()
            emitEvent("All memory cleared")
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearConversations()
            emitEvent("Conversation history cleared")
        }
    }

    suspend fun exportMemoryJson(): String = repository.exportMemoryAsJson()

    fun importMemoryJson(json: String) {
        viewModelScope.launch {
            val imported = repository.importMemoryFromJson(json)
            emitEvent("Imported $imported notes into memory")
        }
    }

    // Diagnostics Data
    fun isNetworkOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val net = cm?.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(net) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun isMicPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isNotificationAccessGranted(context: Context): Boolean {
        return MyraNotificationListenerService.isPermissionGranted(context)
    }

    fun isReadContactsGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isPhoneStateGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun emitEvent(message: String) {
        viewModelScope.launch {
            _uiEvents.emit(message)
        }
    }
}
