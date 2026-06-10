package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- Settings ---
    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getUserSettings(): Flow<UserSettings?>

    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    suspend fun getUserSettingsDirect(): UserSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserSettings(settings: UserSettings)

    // --- Chat Messages ---
    @Query("SELECT * FROM chat_message ORDER BY timestamp ASC")
    fun getChatMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage)

    @Query("DELETE FROM chat_message")
    suspend fun clearChatHistory()

    // --- Memory ---
    @Query("SELECT * FROM memory ORDER BY timestamp DESC")
    fun getMemoryRecords(): Flow<List<MemoryRecord>>

    @Query("SELECT * FROM memory WHERE text LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchMemoryRecords(query: String): Flow<List<MemoryRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemoryRecord(record: MemoryRecord)

    @Delete
    suspend fun deleteMemoryRecord(record: MemoryRecord)

    @Query("DELETE FROM memory WHERE id = :id")
    suspend fun deleteMemoryRecordById(id: Int)

    // --- Automated Apps ---
    @Query("SELECT * FROM automated_app ORDER BY appName ASC")
    fun getAutomatedApps(): Flow<List<AutomatedApp>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAutomatedApp(app: AutomatedApp)

    @Delete
    suspend fun deleteAutomatedApp(app: AutomatedApp)

    // --- Job Applications ---
    @Query("SELECT * FROM job_application ORDER BY id DESC")
    fun getJobApplications(): Flow<List<JobApplication>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobApplication(jobApp: JobApplication)

    @Delete
    suspend fun deleteJobApplication(jobApp: JobApplication)

    // --- Prompt Templates ---
    @Query("SELECT * FROM prompt_template ORDER BY title ASC")
    fun getPromptTemplates(): Flow<List<PromptTemplate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPromptTemplate(template: PromptTemplate)

    @Delete
    suspend fun deletePromptTemplate(template: PromptTemplate)

    // --- Task Items ---
    @Query("SELECT * FROM task_item ORDER BY isEvent DESC, time ASC")
    fun getTaskItems(): Flow<List<TaskItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskItem(task: TaskItem)

    @Delete
    suspend fun deleteTaskItem(task: TaskItem)
}
