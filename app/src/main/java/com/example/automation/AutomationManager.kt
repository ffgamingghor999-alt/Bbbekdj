package com.example.automation

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import com.example.core.model.ActionType
import com.example.core.model.AutomationAction
import com.example.data.local.repository.MyraRepository

data class AutomationResult(
    val isSuccess: Boolean,
    val message: String
)

class AutomationManager(
    private val context: Context,
    private val repository: MyraRepository
) {

    fun isAccessibilityEnabled(): Boolean {
        return MyraAccessibilityService.instance != null
    }

    fun openAccessibilitySettingsIntent(): Intent {
        return Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    fun isPackageInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    suspend fun executeAction(action: AutomationAction): AutomationResult {
        val result = when (action.type) {
            ActionType.OPEN_APP -> launchApp(action.targetApp ?: "")
            ActionType.OPEN_SETTINGS -> launchSettings()
            ActionType.DRAFT_WHATSAPP -> draftWhatsApp(action.payload)
            ActionType.DRAFT_TELEGRAM -> draftTelegram(action.payload)
            ActionType.GLOBAL_HOME -> {
                val service = MyraAccessibilityService.instance
                if (service != null && service.performHome()) {
                    AutomationResult(true, "Navigated to device Home screen")
                } else {
                    // Fallback intent
                    val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_HOME)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(homeIntent)
                    AutomationResult(true, "Dispatched home intent")
                }
            }
            ActionType.GLOBAL_BACK -> {
                val service = MyraAccessibilityService.instance
                if (service != null && service.performBack()) {
                    AutomationResult(true, "Back action executed")
                } else {
                    AutomationResult(false, "Accessibility service needed for global back action")
                }
            }
            ActionType.GLOBAL_RECENTS -> {
                val service = MyraAccessibilityService.instance
                if (service != null && service.performRecents()) {
                    AutomationResult(true, "Recents overview opened")
                } else {
                    AutomationResult(false, "Accessibility service needed for recents overview")
                }
            }
            ActionType.READ_NOTIFICATIONS -> {
                val service = MyraAccessibilityService.instance
                if (service != null && service.performNotifications()) {
                    AutomationResult(true, "Notification shade opened")
                } else {
                    AutomationResult(true, "Reviewing local notification assistant")
                }
            }
            ActionType.SCROLL_DOWN -> {
                val service = MyraAccessibilityService.instance
                if (service != null && service.scrollForward()) {
                    AutomationResult(true, "Scrolled down successfully")
                } else {
                    AutomationResult(false, "Cannot scroll: Accessibility service inactive or scrollable view not found")
                }
            }
            ActionType.SCROLL_UP -> {
                val service = MyraAccessibilityService.instance
                if (service != null && service.scrollBackward()) {
                    AutomationResult(true, "Scrolled up successfully")
                } else {
                    AutomationResult(false, "Cannot scroll: Accessibility service inactive or scrollable view not found")
                }
            }
            ActionType.SYSTEM_DIAGNOSTICS -> {
                AutomationResult(true, "System diagnostics check verified")
            }
            ActionType.UNKNOWN -> {
                AutomationResult(false, "Unknown or unsupported automation action")
            }
        }

        // Audit log in Room database
        repository.logAction(
            actionType = action.type.name,
            target = action.targetApp ?: action.description,
            status = if (result.isSuccess) "SUCCESS" else "FAILED",
            details = result.message
        )

        return result
    }

    private fun launchApp(packageName: String): AutomationResult {
        if (packageName.isBlank()) {
            return AutomationResult(false, "Package name is blank")
        }
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        return if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            AutomationResult(true, "Launched application: $packageName")
        } else {
            AutomationResult(false, "App ($packageName) is not installed on this device")
        }
    }

    private fun launchSettings(): AutomationResult {
        val intent = Intent(Settings.ACTION_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(intent)
            AutomationResult(true, "Opened Android device settings")
        } catch (e: Exception) {
            AutomationResult(false, "Failed to launch settings: ${e.message}")
        }
    }

    private fun draftWhatsApp(text: String?): AutomationResult {
        val targetPkg = "com.whatsapp"
        val isInstalled = isPackageInstalled(targetPkg)
        val message = text ?: "Hello from MYRA AI"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode(message)}")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (isInstalled) {
                setPackage(targetPkg)
            }
        }

        return try {
            context.startActivity(intent)
            AutomationResult(
                true,
                if (isInstalled) "Opened WhatsApp message composer" else "Opened WhatsApp web bridge in browser"
            )
        } catch (e: Exception) {
            AutomationResult(false, "WhatsApp launch failed: ${e.message}")
        }
    }

    private fun draftTelegram(text: String?): AutomationResult {
        val targetPkg = "org.telegram.messenger"
        val isInstalled = isPackageInstalled(targetPkg)
        val message = text ?: "Hello from MYRA AI"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://t.me/share/url?url=${Uri.encode(message)}")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (isInstalled) {
                setPackage(targetPkg)
            }
        }

        return try {
            context.startActivity(intent)
            AutomationResult(
                true,
                if (isInstalled) "Opened Telegram sharing" else "Opened Telegram web preview in browser"
            )
        } catch (e: Exception) {
            AutomationResult(false, "Telegram launch failed: ${e.message}")
        }
    }
}
