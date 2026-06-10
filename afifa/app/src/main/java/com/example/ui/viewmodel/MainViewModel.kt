package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.data.api.GeminiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(database.appDao())
        
        // Initialize settings in Room if empty
        viewModelScope.launch(Dispatchers.IO) {
            repository.initializeSettingsIfNeeded()
        }
    }

    // --- State Streams from Room ---
    val settingsState: StateFlow<UserSettings> = repository.userSettings
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings()
        )

    val chatMessagesState: StateFlow<List<ChatMessage>> = repository.chatMessages
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val memoryRecordsState: StateFlow<List<MemoryRecord>> = repository.memoryRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val automatedAppsState: StateFlow<List<AutomatedApp>> = repository.automatedApps
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val jobApplicationsState: StateFlow<List<JobApplication>> = repository.jobApplications
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val promptTemplatesState: StateFlow<List<PromptTemplate>> = repository.promptTemplates
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val taskItemsState: StateFlow<List<TaskItem>> = repository.taskItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Transient UI States ---
    private val _onboardingStep = MutableStateFlow(1)
    val onboardingStep: StateFlow<Int> = _onboardingStep.asStateFlow()

    private val _isAILinking = MutableStateFlow(true)
    val isAILinking: StateFlow<Boolean> = _isAILinking.asStateFlow()

    private val _voiceState = MutableStateFlow("Completed") // Listening, Thinking, Processing, Speaking, Completed
    val voiceState: StateFlow<String> = _voiceState.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("English")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    // Permissions toggled during onboarding
    private val _permissionsMap = MutableStateFlow(
        mapOf(
            "Microphone" to true,
            "Storage" to true,
            "Contacts" to false,
            "Notifications" to true,
            "Accessibility Services" to false
        )
    )
    val permissionsMap: StateFlow<Map<String, Boolean>> = _permissionsMap.asStateFlow()

    // Confirmation dialog trigger
    private val _pendingActionType = MutableStateFlow<String?>(null)
    val pendingActionType: StateFlow<String?> = _pendingActionType.asStateFlow()
    
    private val _pendingActionText = MutableStateFlow("")
    val pendingActionText: StateFlow<String> = _pendingActionText.asStateFlow()

    private var onPendingActionApproved: (() -> Unit)? = null

    // Memory Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredMemoryState: StateFlow<List<MemoryRecord>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query -> repository.searchMemory(query) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current Navigation Tab in main screen (0 = Chat, 1 = Automation, 2 = Dashboard, 3 = Profile/Settings)
    private val _currentTab = MutableStateFlow(2) // Defaults to Dashboard
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    // --- Onboarding Navigation ---
    fun nextOnboardingStep() {
        if (_onboardingStep.value < 9) {
            _onboardingStep.value += 1
        } else {
            completeOnboarding()
        }
    }

    fun prevOnboardingStep() {
        if (_onboardingStep.value > 1) {
            _onboardingStep.value -= 1
        }
    }

    fun setOnboardingStep(step: Int) {
        _onboardingStep.value = step
    }

    private fun completeOnboarding() {
        viewModelScope.launch(Dispatchers.IO) {
            val s = settingsState.value
            repository.saveUserSettings(s.copy(isOnboardingComplete = true))
        }
    }

    // --- Profile & Setup Updates ---
    fun updateUserName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val s = settingsState.value
            repository.saveUserSettings(s.copy(userName = name))
        }
    }

    fun updateAssistantName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val s = settingsState.value
            repository.saveUserSettings(s.copy(assistantName = name))
        }
    }

    fun updateSelectedVoice(voice: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val s = settingsState.value
            repository.saveUserSettings(s.copy(selectedVoice = voice))
        }
    }

    fun updateActiveModel(model: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val s = settingsState.value
            repository.saveUserSettings(s.copy(activeModel = model))
        }
    }

    fun setLanguage(lang: String) {
        _selectedLanguage.value = lang
    }

    fun togglePermission(permission: String) {
        val current = _permissionsMap.value.toMutableMap()
        current[permission] = !(current[permission] ?: false)
        _permissionsMap.value = current
    }

    fun resetOnboarding() {
        viewModelScope.launch(Dispatchers.IO) {
            _onboardingStep.value = 1
            val currentSettings = settingsState.value
            repository.saveUserSettings(currentSettings.copy(isOnboardingComplete = false))
        }
    }

    // --- Theme System ---
    fun selectThemePreset(presetName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = settingsState.value
            val updated = when (presetName) {
                "Blue Neon" -> current.copy(
                    themePreset = "Blue Neon",
                    customBackgroundHex = "#131313",
                    customAccentHex = "#00daf3",
                    customFontColorHex = "#ffffff",
                    customWaveColorHex = "#00daf3",
                    customButtonColorHex = "#00daf3",
                    glowIntensity = 0.8f
                )
                "White Glow" -> current.copy(
                    themePreset = "White Glow",
                    customBackgroundHex = "#121214",
                    customAccentHex = "#f5f5f7",
                    customFontColorHex = "#ffffff",
                    customWaveColorHex = "#f5f5f7",
                    customButtonColorHex = "#fafafa",
                    glowIntensity = 0.5f
                )
                "Purple Cyber" -> current.copy(
                    themePreset = "Purple Cyber",
                    customBackgroundHex = "#0d0a14",
                    customAccentHex = "#bc3eff",
                    customFontColorHex = "#ffffff",
                    customWaveColorHex = "#cf5cff",
                    customButtonColorHex = "#bc3eff",
                    glowIntensity = 0.9f
                )
                "Green Matrix" -> current.copy(
                    themePreset = "Green Matrix",
                    customBackgroundHex = "#050805",
                    customAccentHex = "#39ff14",
                    customFontColorHex = "#00ff41",
                    customWaveColorHex = "#39ff14",
                    customButtonColorHex = "#00ff41",
                    glowIntensity = 0.7f
                )
                "Red Tech" -> current.copy(
                    themePreset = "Red Tech",
                    customBackgroundHex = "#110b0b",
                    customAccentHex = "#ff1744",
                    customFontColorHex = "#ffffff",
                    customWaveColorHex = "#ff1744",
                    customButtonColorHex = "#ff1744",
                    glowIntensity = 0.8f
                )
                "Gold Premium" -> current.copy(
                    themePreset = "Gold Premium",
                    customBackgroundHex = "#14120e",
                    customAccentHex = "#ffd700",
                    customFontColorHex = "#ffecc6",
                    customWaveColorHex = "#ffca18",
                    customButtonColorHex = "#ffd700",
                    glowIntensity = 1.0f
                )
                else -> current
            }
            repository.saveUserSettings(updated)
        }
    }

    fun updateThemeColors(
        accentHex: String,
        bgHex: String,
        fontHex: String,
        waveHex: String,
        btnHex: String,
        glow: Float
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = settingsState.value
            repository.saveUserSettings(
                current.copy(
                    themePreset = "Custom",
                    customAccentHex = accentHex,
                    customBackgroundHex = bgHex,
                    customFontColorHex = fontHex,
                    customWaveColorHex = waveHex,
                    customButtonColorHex = btnHex,
                    glowIntensity = glow
                )
            )
        }
    }

    // --- Security Dashboard Rules ---
    fun updateSecurityType(type: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = settingsState.value
            repository.saveUserSettings(current.copy(securityType = type))
        }
    }

    // --- Tab Selection ---
    fun selectTab(tab: Int) {
        _currentTab.value = tab
    }

    // --- Chat Send Actions ---
    fun executeImportantAction(actionType: String, actionDetails: String, onApproved: () -> Unit) {
        _pendingActionType.value = actionType
        _pendingActionText.value = actionDetails
        onPendingActionApproved = onApproved
    }

    fun approvePendingAction() {
        onPendingActionApproved?.invoke()
        _pendingActionType.value = null
        _pendingActionText.value = ""
        onPendingActionApproved = null
    }

    fun cancelPendingAction() {
        _pendingActionType.value = null
        _pendingActionText.value = ""
        onPendingActionApproved = null
    }

    fun sendMessage(userPrompt: String) {
        if (userPrompt.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            // Save User message
            val currentSettings = settingsState.value
            val chatMsgUser = ChatMessage(
                sender = "user",
                content = userPrompt,
                model = currentSettings.activeModel
            )
            repository.addChatMessage(chatMsgUser)

            // Transition state to Thinking
            _voiceState.value = "Thinking"
            delay(400)
            _voiceState.value = "Processing"

            // Prompt Gemini via API
            val sysPrompt = "You are ${currentSettings.assistantName}, user's Voice Command World assistant. " +
                    "Customize yourself perfectly for ${currentSettings.userName}. " +
                    "Your active model is ${currentSettings.activeModel}. Your custom language is ${_selectedLanguage.value}. " +
                    "Be highly visual, futuristic, helpful, and JARVIS-inspired."
            
            val responseText = GeminiClient.fetchResponse(prompt = userPrompt, systemPrompt = sysPrompt)

            // Randomly simulate analysis factors for UI richness (e.g. burn rate or retention alerts if prompt asks about business / analytics)
            var bRate: String? = null
            var retRisk: String? = null
            if (userPrompt.contains("market", true) || userPrompt.contains("volatility", true) || userPrompt.contains("cloud", true) || userPrompt.contains("startup", true)) {
                bRate = "+12.4%"
                retRisk = "Moderate"
            }

            _voiceState.value = "Speaking"
            delay(500)

            // Save AI message to database
            val chatMsgAi = ChatMessage(
                sender = "assistant",
                content = responseText,
                model = currentSettings.activeModel,
                sentiment = "Analytical",
                burnRateDelta = bRate,
                retentionRisk = retRisk
            )
            repository.addChatMessage(chatMsgAi)

            // Back up memory automatic sync
            repository.addMemoryRecord(
                MemoryRecord(
                    text = "AFIFA learned: '${if (userPrompt.length > 50) userPrompt.take(50) + "..." else userPrompt}'",
                    category = "Conversations"
                )
            )

            _voiceState.value = "Completed"
        }
    }

    // --- Micro Voice Interaction Actions ---
    fun toggleVoiceMicInteraction() {
        viewModelScope.launch(Dispatchers.Default) {
            if (_voiceState.value == "Completed") {
                _voiceState.value = "Listening"
            } else {
                _voiceState.value = "Thinking"
                delay(800)
                _voiceState.value = "Processing"
                
                // Fetch simulated/Gemini voice response automatically
                val activeWake = settingsState.value.assistantName
                val promptWords = "Voice activation via button. Activating $activeWake neural center."
                sendMessage(promptWords)
            }
        }
    }

    // --- Memory Actions ---
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun insertMemory(info: String, category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addMemoryRecord(MemoryRecord(text = info, category = category))
        }
    }

    fun deleteMemory(record: MemoryRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMemory(record)
        }
    }

    // --- Task Actions ---
    fun saveTask(title: String, time: String, isEvent: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveTaskItem(TaskItem(title = title, time = time, isCompleted = false, isEvent = isEvent))
        }
    }

    fun toggleTaskComplete(task: TaskItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveTaskItem(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: TaskItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTaskItem(task)
        }
    }

    // --- Job Automation ---
    fun applyJobAutomated(jobTitle: String, company: String) {
        executeImportantAction(
            actionType = "Apply Job",
            actionDetails = "Apply for Position '$jobTitle' at '$company' using AFIFA's matched premium profile?"
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.addJobApplication(
                    JobApplication(
                        jobTitle = jobTitle,
                        company = company,
                        matchPercentage = (80..98).random(),
                        status = "Applied",
                        dateString = "Just Now"
                    )
                )
                // Add message confirmation
                repository.addChatMessage(
                    ChatMessage(
                        sender = "assistant",
                        content = "Pipelined completed job application for '$jobTitle' at '$company' successfully. Tracking has been added to temporal Memory."
                    )
                )
            }
        }
    }

    fun postUpdateAutomated(platformName: String, textContent: String) {
        executeImportantAction(
            actionType = "Post Content",
            actionDetails = "Post social update to $platformName:\n\"$textContent\""
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.addChatMessage(
                    ChatMessage(
                        sender = "assistant",
                        content = "Successfully dispatched post authorization to $platformName core integration."
                    )
                )
            }
        }
    }

    fun deleteMemoryCenterData() {
        executeImportantAction(
            actionType = "Delete Data",
            actionDetails = "Wipe and purge your entire local Encrypted Storage memory history? This is an irreversible action."
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.clearChat()
                // Purge memory
                val records = repository.memoryRecords.firstOrNull() ?: emptyList()
                for (rec in records) {
                    repository.deleteMemory(rec)
                }
                repository.addChatMessage(
                    ChatMessage(
                        sender = "assistant",
                        content = "System Purge Completed. Neural vectors completely zeroed."
                    )
                )
            }
        }
    }

    fun deleteAutomatedApp(app: AutomatedApp) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAutomatedApp(app)
        }
    }

    fun addAutomatedAppManual(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addAutomatedApp(AutomatedApp(appName = name, activeFlows = (1..5).random()))
        }
    }
}
