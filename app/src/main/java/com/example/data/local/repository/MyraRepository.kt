package com.example.data.local.repository

import com.example.data.local.dao.ActionLogDao
import com.example.data.local.dao.ConversationDao
import com.example.data.local.dao.MemoryNoteDao
import com.example.data.local.entity.ActionLogEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.MemoryNoteEntity
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

class MyraRepository(
    private val conversationDao: ConversationDao,
    private val memoryNoteDao: MemoryNoteDao,
    private val actionLogDao: ActionLogDao
) {
    // Conversations
    val allConversations: Flow<List<ConversationEntity>> = conversationDao.getAllConversations()
    fun getRecentConversations(limit: Int = 10): Flow<List<ConversationEntity>> =
        conversationDao.getRecentConversations(limit)

    suspend fun saveMessage(
        text: String,
        isUser: Boolean,
        languageCode: String = "en",
        suggestedAction: String? = null
    ): Long {
        return conversationDao.insertMessage(
            ConversationEntity(
                text = text,
                isUser = isUser,
                languageCode = languageCode,
                suggestedAction = suggestedAction
            )
        )
    }

    suspend fun deleteMessage(id: Long) = conversationDao.deleteMessageById(id)
    suspend fun clearConversations() = conversationDao.clearAllConversations()
    suspend fun getConversationCount(): Int = conversationDao.getCount()

    // Memory Notes
    val allNotes: Flow<List<MemoryNoteEntity>> = memoryNoteDao.getAllNotes()
    fun searchNotes(query: String): Flow<List<MemoryNoteEntity>> = memoryNoteDao.searchNotes(query)

    suspend fun saveNote(title: String, content: String, category: String = "General"): Long {
        return memoryNoteDao.insertNote(
            MemoryNoteEntity(
                title = title,
                content = content,
                category = category
            )
        )
    }

    suspend fun deleteNote(id: Long) = memoryNoteDao.deleteNoteById(id)
    suspend fun clearAllNotes() = memoryNoteDao.clearAllNotes()
    suspend fun getNotesCount(): Int = memoryNoteDao.getCount()

    suspend fun exportMemoryAsJson(): String {
        val notes = memoryNoteDao.getAllNotesDirect()
        val jsonArray = JSONArray()
        for (note in notes) {
            val obj = JSONObject()
            obj.put("id", note.id)
            obj.put("title", note.title)
            obj.put("content", note.content)
            obj.put("category", note.category)
            obj.put("timestamp", note.timestamp)
            jsonArray.put(obj)
        }
        return jsonArray.toString(2)
    }

    suspend fun importMemoryFromJson(jsonString: String): Int {
        var count = 0
        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val title = obj.optString("title", "Imported Note")
                val content = obj.optString("content", "")
                val category = obj.optString("category", "Imported")
                if (content.isNotBlank()) {
                    saveNote(title, content, category)
                    count++
                }
            }
        } catch (_: Exception) {
            // Invalid JSON format handled gracefully
        }
        return count
    }

    // Action Logs
    val allActionLogs: Flow<List<ActionLogEntity>> = actionLogDao.getAllLogs()
    fun getRecentActionLogs(limit: Int = 10): Flow<List<ActionLogEntity>> =
        actionLogDao.getRecentLogs(limit)

    suspend fun logAction(
        actionType: String,
        target: String,
        status: String,
        details: String
    ): Long {
        return actionLogDao.insertLog(
            ActionLogEntity(
                actionType = actionType,
                target = target,
                status = status,
                details = details
            )
        )
    }

    suspend fun clearActionLogs() = actionLogDao.clearAllLogs()
    suspend fun getActionLogCount(): Int = actionLogDao.getCount()
}
