package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ai.LocalAiEngine
import com.example.core.model.ActionType
import com.example.core.model.Language
import com.example.data.local.MyraDatabase
import com.example.data.local.repository.MyraRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var context: Context
    private lateinit var database: MyraDatabase
    private lateinit var repository: MyraRepository
    private lateinit var localAiEngine: LocalAiEngine

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        database = MyraDatabase.getInstance(context)
        repository = MyraRepository(
            conversationDao = database.conversationDao(),
            memoryNoteDao = database.memoryNoteDao(),
            actionLogDao = database.actionLogDao()
        )
        localAiEngine = LocalAiEngine(context)
    }

    @Test
    fun testAppNameString() {
        val appName = context.getString(R.string.app_name)
        assertEquals("MYRA AI", appName)
    }

    @Test
    fun testLocalAiEngineWhatsAppIntent() {
        val response = localAiEngine.processQuery("MYRA open WhatsApp", Language.ENGLISH)
        assertNotNull(response.suggestedAction)
        assertEquals(ActionType.DRAFT_WHATSAPP, response.suggestedAction?.type)
        assertTrue(response.replyText.contains("WhatsApp", ignoreCase = true))
    }

    @Test
    fun testLocalAiEngineBengaliQuery() {
        val response = localAiEngine.processQuery("হোয়াটসঅ্যাপ খোলো", Language.BENGALI)
        assertNotNull(response.suggestedAction)
        assertEquals(ActionType.DRAFT_WHATSAPP, response.suggestedAction?.type)
        assertTrue(response.replyText.contains("হোয়াটসঅ্যাপ"))
    }

    @Test
    fun testLocalAiEngineMathEvaluation() {
        val response = localAiEngine.processQuery("What is 25 + 75?", Language.ENGLISH)
        assertTrue(response.replyText.contains("100"))
    }

    @Test
    fun testRepositoryDatabaseOperations() = runBlocking {
        // Conversation test
        val msgId = repository.saveMessage(
            text = "Test message from unit test",
            isUser = true,
            languageCode = "en"
        )
        assertTrue(msgId > 0)
        val messages = repository.allConversations.first()
        assertTrue(messages.any { it.text == "Test message from unit test" })

        // Memory note test
        val noteId = repository.saveNote(
            title = "Doctor Appointment",
            content = "Tuesday at 4 PM in Apollo",
            category = "Health"
        )
        assertTrue(noteId > 0)
        val notes = repository.allNotes.first()
        assertTrue(notes.any { it.title == "Doctor Appointment" })

        // Export/Import test
        val json = repository.exportMemoryAsJson()
        assertTrue(json.contains("Doctor Appointment"))

        val importedCount = repository.importMemoryFromJson(json)
        assertTrue(importedCount > 0)
    }
}
