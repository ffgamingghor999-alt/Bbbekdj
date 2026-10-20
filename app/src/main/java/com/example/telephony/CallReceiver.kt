package com.example.telephony

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import com.example.MyraApplication
import com.example.core.model.Language

object CallAnnouncementSettings {
    var isEnabled: Boolean = false
    var announceUnknownNumbers: Boolean = true
    private var lastAnnouncementTime: Long = 0

    fun canAnnounceNow(): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastAnnouncementTime > 6000) {
            lastAnnouncementTime = now
            return true
        }
        return false
    }
}

class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (!CallAnnouncementSettings.isEnabled) return

        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                if (!CallAnnouncementSettings.canAnnounceNow()) return

                val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                val callerName = resolveContactName(context, incomingNumber)

                val announcement = when {
                    !callerName.isNullOrBlank() -> "Incoming call from $callerName"
                    !incomingNumber.isNullOrBlank() && CallAnnouncementSettings.announceUnknownNumbers ->
                        "Incoming call from ${formatNumberForSpeech(incomingNumber)}"
                    else -> "Incoming call from an unknown number"
                }

                try {
                    val app = context.applicationContext as? MyraApplication
                    app?.voiceManager?.speak(announcement, Language.ENGLISH)
                } catch (_: Exception) {}
            }
        }
    }

    private fun resolveContactName(context: Context, phoneNumber: String?): String? {
        if (phoneNumber.isNullOrBlank()) return null
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        return try {
            val uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber)
            )
            val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        return cursor.getString(nameIndex)
                    }
                }
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    private fun formatNumberForSpeech(number: String): String {
        return number.map { it.toString() }.joinToString(" ")
    }
}
