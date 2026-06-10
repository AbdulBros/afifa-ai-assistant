package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class AppRepository(private val appDao: AppDao) {

    val userSettings: Flow<UserSettings?> = appDao.getUserSettings()

    // Initialize with default settings if empty
    suspend fun initializeSettingsIfNeeded() {
        val current = appDao.getUserSettingsDirect()
        if (current == null) {
            appDao.saveUserSettings(UserSettings())
            
            // Add some initial automated apps
            appDao.insertAutomatedApp(AutomatedApp(appName = "WhatsApp", packageName = "com.whatsapp", activeFlows = 3))
            appDao.insertAutomatedApp(AutomatedApp(appName = "Instagram", packageName = "com.instagram", activeFlows = 2))
            appDao.insertAutomatedApp(AutomatedApp(appName = "Gmail", packageName = "com.google.android.gm", activeFlows = 4))
            appDao.insertAutomatedApp(AutomatedApp(appName = "SMS / Messenger", packageName = "com.android.messaging", activeFlows = 1))

            // Add some typical prompts
            appDao.insertPromptTemplate(PromptTemplate(title = "App Pitch", content = "Write a high-converting landing page pitch for an AI app named [AppName]...", category = "Apps"))
            appDao.insertPromptTemplate(PromptTemplate(title = "Fix Kotlin Null Pointer", content = "Explain why this block causes NullPointerException and rewrite it cleanly in modern style...", category = "Games"))
            appDao.insertPromptTemplate(PromptTemplate(title = "Marketing Strategy", content = "Generate a 4-week omnichannel marketing rollout roadmap targeting developer tools...", category = "Marketing"))

            // Add some default tasks
            appDao.insertTaskItem(TaskItem(title = "Neural Network Synchronization", time = "09:00", isCompleted = true, isEvent = true))
            appDao.insertTaskItem(TaskItem(title = "Client Architecture Review", time = "11:30", isCompleted = false, isEvent = true))
            appDao.insertTaskItem(TaskItem(title = "Optimize vector database cache", time = "14:00", isCompleted = true, isEvent = false))
            appDao.insertTaskItem(TaskItem(title = "Review new API endpoints and schemas", time = "16:15", isCompleted = false, isEvent = false))

            // Add initial greeting from AFIFA
            appDao.insertChatMessage(
                ChatMessage(
                    sender = "assistant",
                    content = "Initializing secure session. I am AFIFA, your neural interface. How may I assist your workflow today?",
                    model = "Gemini 3.5 Flash",
                    sentiment = "Neutral"
                )
            )
        }
    }

    suspend fun saveUserSettings(settings: UserSettings) {
        appDao.saveUserSettings(settings)
    }

    // --- Chat Messages ---
    val chatMessages: Flow<List<ChatMessage>> = appDao.getChatMessages()

    suspend fun addChatMessage(message: ChatMessage) {
        appDao.insertChatMessage(message)
    }

    suspend fun clearChat() {
        appDao.clearChatHistory()
    }

    // --- Memory ---
    val memoryRecords: Flow<List<MemoryRecord>> = appDao.getMemoryRecords()

    fun searchMemory(query: String): Flow<List<MemoryRecord>> {
        return if (query.isBlank()) {
            appDao.getMemoryRecords()
        } else {
            appDao.searchMemoryRecords(query)
        }
    }

    suspend fun addMemoryRecord(record: MemoryRecord) {
        appDao.insertMemoryRecord(record)
    }

    suspend fun deleteMemory(record: MemoryRecord) {
        appDao.deleteMemoryRecord(record)
    }

    suspend fun deleteMemoryById(id: Int) {
        appDao.deleteMemoryRecordById(id)
    }

    // --- Automated Apps ---
    val automatedApps: Flow<List<AutomatedApp>> = appDao.getAutomatedApps()

    suspend fun addAutomatedApp(app: AutomatedApp) {
        appDao.insertAutomatedApp(app)
    }

    suspend fun deleteAutomatedApp(app: AutomatedApp) {
        appDao.deleteAutomatedApp(app)
    }

    // --- Job Applications ---
    val jobApplications: Flow<List<JobApplication>> = appDao.getJobApplications()

    suspend fun addJobApplication(jobApp: JobApplication) {
        appDao.insertJobApplication(jobApp)
    }

    suspend fun deleteJobApplication(jobApp: JobApplication) {
        appDao.deleteJobApplication(jobApp)
    }

    // --- Prompt Templates ---
    val promptTemplates: Flow<List<PromptTemplate>> = appDao.getPromptTemplates()

    suspend fun addPromptTemplate(template: PromptTemplate) {
        appDao.insertPromptTemplate(template)
    }

    suspend fun deletePromptTemplate(template: PromptTemplate) {
        appDao.deletePromptTemplate(template)
    }

    // --- Tasks & Calendar ---
    val taskItems: Flow<List<TaskItem>> = appDao.getTaskItems()

    suspend fun saveTaskItem(task: TaskItem) {
        appDao.insertTaskItem(task)
    }

    suspend fun deleteTaskItem(task: TaskItem) {
        appDao.deleteTaskItem(task)
    }
}
