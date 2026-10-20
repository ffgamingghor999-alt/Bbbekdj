package com.example

import android.app.Application
import com.example.ai.AiBrain
import com.example.ai.LocalAiEngine
import com.example.ai.OnlineAiEngine
import com.example.automation.AutomationManager
import com.example.data.local.MyraDatabase
import com.example.data.local.repository.MyraRepository
import com.example.voice.VoiceManager

class MyraApplication : Application() {

    lateinit var database: MyraDatabase
        private set

    lateinit var repository: MyraRepository
        private set

    lateinit var localAiEngine: LocalAiEngine
        private set

    lateinit var onlineAiEngine: OnlineAiEngine
        private set

    lateinit var aiBrain: AiBrain
        private set

    lateinit var voiceManager: VoiceManager
        private set

    lateinit var automationManager: AutomationManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = MyraDatabase.getInstance(this)
        repository = MyraRepository(
            conversationDao = database.conversationDao(),
            memoryNoteDao = database.memoryNoteDao(),
            actionLogDao = database.actionLogDao()
        )

        localAiEngine = LocalAiEngine(this)
        onlineAiEngine = OnlineAiEngine()
        aiBrain = AiBrain(localAiEngine, onlineAiEngine, repository)

        voiceManager = VoiceManager(this)
        automationManager = AutomationManager(this, repository)
    }

    override fun onTerminate() {
        super.onTerminate()
        voiceManager.shutdown()
    }

    companion object {
        lateinit var instance: MyraApplication
            private set
    }
}
