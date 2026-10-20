package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "action_logs")
data class ActionLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val actionType: String,
    val target: String,
    val status: String, // "SUCCESS", "FAILED", "CONFIRMATION_REQUIRED", "CANCELLED"
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)
