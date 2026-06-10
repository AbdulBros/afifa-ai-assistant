package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey val id: Int = 1,
    val userName: String = "User",
    val assistantName: String = "Afifa",
    val selectedVoice: String = "Female",
    val activeModel: String = "Gemini 3.5 Flash",
    val themePreset: String = "Blue Neon",
    val customBackgroundHex: String = "#131313",
    val customAccentHex: String = "#00daf3",
    val customFontColorHex: String = "#ffffff",
    val customWaveColorHex: String = "#00daf3",
    val customButtonColorHex: String = "#00daf3",
    val glowIntensity: Float = 0.8f,
    val isOnboardingComplete: Boolean = false,
    val securityType: String = "NONE" // NONE, PIN, BIOMETRIC, VOICE
)

@Entity(tableName = "chat_message")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // "user", "assistant"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val model: String = "",
    val sentiment: String? = null,
    val burnRateDelta: String? = null,
    val retentionRisk: String? = null
)

@Entity(tableName = "memory")
data class MemoryRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val category: String = "General", // Conversations, Tasks, Preference, Job
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "automated_app")
data class AutomatedApp(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val appName: String,
    val packageName: String = "",
    val activeFlows: Int = 0,
    val isEnabled: Boolean = true
)

@Entity(tableName = "job_application")
data class JobApplication(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val jobTitle: String,
    val company: String,
    val matchPercentage: Int = 85,
    val status: String = "Applied", // "Found", "Applied", "Interview", "Offer"
    val dateString: String = "Today",
    val trackingNotes: String = ""
)

@Entity(tableName = "prompt_template")
data class PromptTemplate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val category: String = "General",
    val isUserCustom: Boolean = false
)

@Entity(tableName = "task_item")
data class TaskItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val time: String, // e.g. "09:00"
    val isCompleted: Boolean = false,
    val isEvent: Boolean = false // false = todo, true = calendar event
)
