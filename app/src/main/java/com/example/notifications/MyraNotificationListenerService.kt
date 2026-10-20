package com.example.notifications

import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.core.model.NotificationItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class MyraNotificationListenerService : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        _isListenerConnected.value = true
        fetchActiveNotifications()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        _isListenerConnected.value = false
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn ?: return

        // Skip our own notifications to prevent loops
        if (sbn.packageName == packageName) return

        val extras = sbn.notification.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
            ?: ""

        if (title.isBlank() && text.isBlank()) return

        val appName = try {
            val pm = packageManager
            val appInfo = pm.getApplicationInfo(sbn.packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (_: Exception) {
            sbn.packageName
        }

        val item = NotificationItem(
            id = "${sbn.id}_${sbn.postTime}_${UUID.randomUUID()}",
            packageName = sbn.packageName,
            appName = appName,
            title = if (privacyModeEnabled) "[Private Sender]" else title,
            text = if (privacyModeEnabled) "[Notification content hidden in Privacy Mode]" else text,
            timestamp = sbn.postTime
        )

        val currentList = _capturedNotifications.value.toMutableList()
        currentList.add(0, item)
        // Keep max 50 recent notifications
        if (currentList.size > 50) {
            currentList.removeAt(currentList.lastIndex)
        }
        _capturedNotifications.value = currentList
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }

    private fun fetchActiveNotifications() {
        try {
            val active = activeNotifications ?: return
            val items = mutableListOf<NotificationItem>()
            for (sbn in active) {
                if (sbn.packageName == packageName) continue
                val extras = sbn.notification.extras ?: continue
                val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
                val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
                if (title.isNotBlank() || text.isNotBlank()) {
                    val appName = try {
                        val pm = packageManager
                        val appInfo = pm.getApplicationInfo(sbn.packageName, 0)
                        pm.getApplicationLabel(appInfo).toString()
                    } catch (_: Exception) {
                        sbn.packageName
                    }
                    items.add(
                        NotificationItem(
                            id = "${sbn.id}_${sbn.postTime}",
                            packageName = sbn.packageName,
                            appName = appName,
                            title = if (privacyModeEnabled) "[Private Sender]" else title,
                            text = if (privacyModeEnabled) "[Content Hidden]" else text,
                            timestamp = sbn.postTime
                        )
                    )
                }
            }
            _capturedNotifications.value = items
        } catch (_: Exception) {}
    }

    companion object {
        private val _isListenerConnected = MutableStateFlow(false)
        val isListenerConnected: StateFlow<Boolean> = _isListenerConnected.asStateFlow()

        private val _capturedNotifications = MutableStateFlow<List<NotificationItem>>(emptyList())
        val capturedNotifications: StateFlow<List<NotificationItem>> = _capturedNotifications.asStateFlow()

        var privacyModeEnabled: Boolean = false

        fun clearNotifications() {
            _capturedNotifications.value = emptyList()
        }

        fun isPermissionGranted(context: Context): Boolean {
            val enabledListeners = Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners"
            ) ?: return false
            val myComponent = ComponentName(context, MyraNotificationListenerService::class.java).flattenToString()
            return enabledListeners.contains(myComponent)
        }

        fun openSettingsIntent(): Intent {
            return Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
    }
}
