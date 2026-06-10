package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.ThemeHelper
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- CENTRAL DOCK HUB WRAPPER ---

@Composable
fun MainApplicationHub(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val settings by viewModel.settingsState.collectAsState()
    val pendingActionType by viewModel.pendingActionType.collectAsState()
    val pendingActionText by viewModel.pendingActionText.collectAsState()

    val accentColor = ThemeHelper.getAccentColor(settings)
    val backgroundColor = ThemeHelper.getBackgroundColor(settings)
    val fontColor = ThemeHelper.getFontColor(settings)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Background subtle neon energy pulses
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(100.dp)
                .alpha(0.08f)
        ) {
            Box(
                modifier = Modifier
                    .size(400.dp)
                    .align(Alignment.TopStart)
                    .background(accentColor, CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(350.dp)
                    .align(Alignment.BottomEnd)
                    .background(accentColor, CircleShape)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Neural Connection Icon",
                        tint = accentColor,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AFIFA",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        letterSpacing = 4.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(100.dp))
                            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(100.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(accentColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = settings.activeModel.uppercase(),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = accentColor,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // Avatar Circle
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(accentColor.copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, accentColor.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User",
                            tint = fontColor.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Central active tab content area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                when (currentTab) {
                    0 -> ChatTabScreen(viewModel, settings, accentColor, fontColor)
                    1 -> AutomationTabScreen(viewModel, settings, accentColor, fontColor)
                    2 -> DashboardTabHub(viewModel, settings, accentColor, fontColor)
                    3 -> SettingsTabScreen(viewModel, settings, accentColor, fontColor)
                }
            }

            // Bottom Navigation Dock
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .border(BorderStroke(1.dp, fontColor.copy(alpha = 0.08f)), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .navigationBarsPadding()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    NavigationItem("Chat", Icons.Default.Email, 0),
                    NavigationItem("Automation", Icons.Default.Settings, 1),
                    NavigationItem("Dashboard", Icons.Default.Menu, 2),
                    NavigationItem("Profile", Icons.Default.Person, 3)
                ).forEach { item ->
                    val isSelected = currentTab == item.index
                    Column(
                        modifier = Modifier
                            .clickable { viewModel.selectTab(item.index) }
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) accentColor else fontColor.copy(alpha = 0.4f),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.label,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) accentColor else fontColor.copy(alpha = 0.4f),
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        // --- GLOBAL OVERLAYS (Confirmation Dialog, Rule Triggers) ---
        if (pendingActionType != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1c1c1f)),
                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alert Action Security check",
                            tint = Color.Red,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "SECURITY AUTHORIZATION",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "AFIFA requires explicit confirmation before executing this rule on your device:",
                            fontSize = 14.sp,
                            color = fontColor.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .border(BorderStroke(1.dp, fontColor.copy(alpha = 0.08f)), RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = pendingActionText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.cancelPendingAction() },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = fontColor.copy(0.6f)),
                                border = BorderStroke(1.dp, fontColor.copy(0.15f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                            ) {
                                Text(text = "CANCEL", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.approvePendingAction() },
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(50.dp)
                            ) {
                                Text(text = "CONFIRM & SEND", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class NavigationItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val index: Int)

// --- CHAT INTERFACE TAB SCREEN ---

@Composable
fun ChatTabScreen(
    viewModel: MainViewModel,
    settings: UserSettings,
    accentColor: Color,
    fontColor: Color
) {
    val messages by viewModel.chatMessagesState.collectAsState()
    val voiceState by viewModel.voiceState.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Languages available for chat dropdown selectors
    val languages = listOf("English", "Tamil", "Hindi", "Telugu", "Malayalam", "Kannada", "Bengali", "Arabic", "French", "German", "Spanish")
    var expandLangMenu by remember { mutableStateOf(false) }

    // Auto-scroll to lowest chats
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Status indicator details row (Languages, Security, States)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Memory indicator
            Box(
                modifier = Modifier
                    .background(Color(0xFF002B30), RoundedCornerShape(100.dp))
                    .border(BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)), RoundedCornerShape(100.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Memory state",
                        tint = accentColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "MEMORY AWARENESS HIGH",
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        color = accentColor,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Language Selector Box
            Box {
                Box(
                    modifier = Modifier
                        .background(fontColor.copy(alpha = 0.03f), RoundedCornerShape(100.dp))
                        .border(BorderStroke(1.dp, fontColor.copy(alpha = 0.08f)), RoundedCornerShape(100.dp))
                        .clickable { expandLangMenu = !expandLangMenu }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Language",
                            tint = fontColor.copy(alpha = 0.6f),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = selectedLanguage.uppercase(),
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            color = fontColor.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Open Language selections",
                            tint = fontColor.copy(alpha = 0.4f),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = expandLangMenu,
                    onDismissRequest = { expandLangMenu = false },
                    modifier = Modifier.background(Color(0xFF1c1c1f))
                ) {
                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(text = lang, color = fontColor) },
                            onClick = {
                                viewModel.setLanguage(lang)
                                expandLangMenu = false
                            }
                        )
                    }
                }
            }

            // Security Encryption Indicator
            Box(
                modifier = Modifier
                    .background(fontColor.copy(alpha = 0.03f), RoundedCornerShape(100.dp))
                    .border(BorderStroke(1.dp, fontColor.copy(alpha = 0.08f)), RoundedCornerShape(100.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Secure status symbol",
                        tint = fontColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ENCRYPTED",
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        color = fontColor.copy(alpha = 0.5f),
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Conversation messages log stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.sender == "user"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        if (!isUser) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(28.dp)
                                    .background(accentColor.copy(alpha = 0.1f), CircleShape)
                                    .border(BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "AFIFA Icon",
                                    tint = accentColor,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(
                                topStart = if (isUser) 16.dp else 4.dp,
                                topEnd = if (isUser) 4.dp else 16.dp,
                                bottomStart = 16.dp,
                                bottomEnd = 16.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUser) accentColor.copy(alpha = 0.08f) else fontColor.copy(alpha = 0.03f)
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isUser) accentColor.copy(alpha = 0.25f) else fontColor.copy(alpha = 0.06f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = msg.content,
                                    fontSize = 15.sp,
                                    color = if (isUser) accentColor else fontColor,
                                    lineHeight = 22.sp
                                )

                                // Real-time analytical parameters widgets if included
                                if (!isUser && msg.burnRateDelta != null) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(fontColor.copy(alpha = 0.02f), RoundedCornerShape(8.dp))
                                                .border(BorderStroke(1.dp, fontColor.copy(alpha = 0.06f)), RoundedCornerShape(8.dp))
                                                .padding(8.dp)
                                        ) {
                                            Column {
                                                Text("BURN RATE DELTA", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = accentColor.copy(alpha = 0.5f))
                                                Text(msg.burnRateDelta, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = fontColor)
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(fontColor.copy(alpha = 0.02f), RoundedCornerShape(8.dp))
                                                .border(BorderStroke(1.dp, fontColor.copy(alpha = 0.06f)), RoundedCornerShape(8.dp))
                                                .padding(8.dp)
                                        ) {
                                            Column {
                                                Text("RETENTION RISK", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = fontColor.copy(alpha = 0.4f))
                                                Text(msg.retentionRisk ?: "Low", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = fontColor)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Processing indicators ticker
            if (voiceState == "Thinking" || voiceState == "Processing") {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(accentColor.copy(alpha = 0.05f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Active process icon",
                                tint = accentColor.copy(alpha = 0.4f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${settings.assistantName} is processing neural layers...",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = fontColor.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }

        // Voice state wave simulation bar
        if (voiceState != "Completed") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .background(accentColor.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Waveform elements simulation
                val waveCount = 7
                val infiniteTransition = rememberInfiniteTransition(label = "voice_trans")
                
                Text(
                    text = "${voiceState.uppercase()}:",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(24.dp)
                ) {
                    for (i in 0 until waveCount) {
                        val duration = 800 + i * 150
                        val heightMultiplier by infiniteTransition.animateFloat(
                            initialValue = 0.2f,
                            targetValue = 1.0f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(duration, easing = EaseInOutBounce),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "wave_ele_$i"
                        )
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .fillMaxHeight(heightMultiplier)
                                .clip(RoundedCornerShape(100.dp))
                                .background(accentColor)
                        )
                    }
                }
            }
        }

        // Lower text messaging input bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = {
                    Text(
                        text = "Command ${settings.assistantName}...",
                        color = fontColor.copy(alpha = 0.35f),
                        fontSize = 14.sp
                    )
                },
                trailingIcon = {
                    if (textInput.isNotBlank()) {
                        IconButton(onClick = {
                            viewModel.sendMessage(textInput)
                            textInput = ""
                        }) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Dispatch message",
                                tint = accentColor
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = fontColor.copy(alpha = 0.02f),
                    unfocusedContainerColor = fontColor.copy(alpha = 0.02f),
                    focusedIndicatorColor = accentColor,
                    unfocusedIndicatorColor = fontColor.copy(alpha = 0.1f),
                    focusedTextColor = fontColor,
                    unfocusedTextColor = fontColor
                ),
                shape = RoundedCornerShape(24.dp),
                textStyle = TextStyle(fontSize = 14.sp),
                modifier = Modifier.weight(1f)
            )

            // Voice mic activation orb trigger
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(accentColor)
                    .clickable { viewModel.toggleVoiceMicInteraction() }
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Speak loop trigger",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// --- AUTOMATION INTEGRATION SECTION TAB ---

@Composable
fun AutomationTabScreen(
    viewModel: MainViewModel,
    settings: UserSettings,
    accentColor: Color,
    fontColor: Color
) {
    val apps by viewModel.automatedAppsState.collectAsState()
    var appText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "AUTOMATION FLUID ENGINE",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = accentColor,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Active Automation Center",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = fontColor
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Automate secondary app inputs via AFIFA pipelines. Rules contain security check confirmations.",
            fontSize = 13.sp,
            color = fontColor.copy(alpha = 0.6f),
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Post updates trigger simulator box
        Card(
            colors = CardDefaults.cardColors(containerColor = fontColor.copy(alpha = 0.02f)),
            border = BorderStroke(1.dp, fontColor.copy(alpha = 0.08f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "DISPATCH SOCIAL AUTOMATIONS",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        viewModel.postUpdateAutomated(
                            "LinkedIn",
                            "Thrilled to launch AFIFA personal synth neural system. Speed 14ms latency!"
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Dissemination test", tint = accentColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SIMULATE AUTO LINKEDIN TRANSMISSION",
                        color = accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Custom Add App Input Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = appText,
                onValueChange = { appText = it },
                placeholder = { Text("App Name (e.g. Snapchat)", fontSize = 13.sp, color = fontColor.copy(alpha = 0.4f)) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = fontColor.copy(alpha = 0.02f),
                    unfocusedContainerColor = fontColor.copy(alpha = 0.02f),
                    focusedIndicatorColor = accentColor
                ),
                shape = RoundedCornerShape(12.dp),
                textStyle = TextStyle(fontSize = 14.sp),
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    if (appText.isNotBlank()) {
                        viewModel.addAutomatedAppManual(appText)
                        appText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text("ADD APP", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "SECURED APPS INDEX",
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            color = fontColor.copy(alpha = 0.4f),
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Installed/Configured app list
        apps.forEach { app ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(fontColor.copy(alpha = 0.02f))
                    .border(BorderStroke(1.dp, fontColor.copy(alpha = 0.06f)), RoundedCornerShape(12.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(accentColor.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "App vector logo",
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = app.appName, fontWeight = FontWeight.Bold, color = fontColor)
                        Text(
                            text = "AFIFA flow threads: ${app.activeFlows}",
                            fontSize = 11.sp,
                            color = fontColor.copy(alpha = 0.5f)
                        )
                    }
                }

                IconButton(onClick = { viewModel.deleteAutomatedApp(app) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete configuration",
                        tint = Color.Red.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

// --- DASHBOARD PORTAL BENTO CORE HUB ---

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardTabHub(
    viewModel: MainViewModel,
    settings: UserSettings,
    accentColor: Color,
    fontColor: Color
) {
    val tasks by viewModel.taskItemsState.collectAsState()
    val apps by viewModel.automatedAppsState.collectAsState()
    val records by viewModel.memoryRecordsState.collectAsState()
    val templates by viewModel.promptTemplatesState.collectAsState()
    val jobApplications by viewModel.jobApplicationsState.collectAsState()

    var customMemoryInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Section Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Wake word banner
                Box(
                    modifier = Modifier
                        .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(100.dp))
                        .border(BorderStroke(1.dp, accentColor), RoundedCornerShape(100.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(accentColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "WAKE WORD: HI ${settings.assistantName.uppercase()}",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            letterSpacing = 2.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Assistant Name: ${settings.assistantName}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = fontColor,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Initializing secure session. All neural intelligence modules are synchronized and live.",
                    fontSize = 13.sp,
                    color = fontColor.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }

        // BENTO BOX 1: AI Chat Quick Access Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = fontColor.copy(alpha = 0.03f)),
                border = BorderStroke(1.dp, fontColor.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectTab(0) }
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Active chat core",
                                tint = accentColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "PRIORITY_01",
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                color = accentColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "AI Chat",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = fontColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Connect with the core consciousness for advanced reasoning, creative brainstorming, and empathetic dialogue.",
                        fontSize = 13.sp,
                        color = fontColor.copy(alpha = 0.5f),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // BENTO BOX 2: Memory Storage Module
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = fontColor.copy(alpha = 0.03f)),
                border = BorderStroke(1.dp, fontColor.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = "Memory index",
                                tint = accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Memory Center",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = fontColor
                            )
                        }

                        IconButton(onClick = { viewModel.deleteMemoryCenterData() }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear all database items",
                                tint = Color.Red.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Long-term contextual storage. Purge custom variables securely anytime.",
                        fontSize = 12.sp,
                        color = fontColor.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = customMemoryInput,
                            onValueChange = { customMemoryInput = it },
                            placeholder = { Text("Add variable...", fontSize = 12.sp, color = fontColor.copy(alpha = 0.4f)) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = accentColor
                            ),
                            textStyle = TextStyle(fontSize = 13.sp),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (customMemoryInput.isNotBlank()) {
                                    viewModel.insertMemory(customMemoryInput, "General")
                                    customMemoryInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("SAVE", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "SAVED MEMORIES INDEX (${records.size})",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = fontColor.copy(alpha = 0.4f),
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        records.take(3).forEach { rec ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(fontColor.copy(alpha = 0.015f), RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    rec.text,
                                    fontSize = 12.sp,
                                    color = fontColor.copy(alpha = 0.8f),
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Delete",
                                    tint = fontColor.copy(alpha = 0.3f),
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { viewModel.deleteMemory(rec) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // BENTO BOX 3: Temporal Schedule & Checklists
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = fontColor.copy(alpha = 0.03f)),
                border = BorderStroke(1.dp, fontColor.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Calendar scheduler logo",
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Temporal Schedule & Checklist",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = fontColor
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "TODAY'S WORKSPACE AGENDA",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        tasks.forEach { task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(fontColor.copy(alpha = 0.02f))
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            if (task.isCompleted) Color.Transparent else fontColor.copy(alpha = 0.08f)
                                        ),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.toggleTaskComplete(task) }
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.MoreVert,
                                        contentDescription = "Check state",
                                        tint = if (task.isCompleted) accentColor else fontColor.copy(alpha = 0.3f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = task.title,
                                        fontSize = 13.sp,
                                        color = if (task.isCompleted) fontColor.copy(alpha = 0.4f) else fontColor,
                                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                    )
                                }

                                Text(
                                    text = task.time,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (task.isCompleted) fontColor.copy(alpha = 0.3f) else accentColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // BENTO BOX 4: Dev Studio (Coding Grid)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = fontColor.copy(alpha = 0.03f)),
                border = BorderStroke(1.dp, fontColor.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Code Assistant Logo",
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AFIFA Dev Studio",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = fontColor
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pipelined micro compiler for Flutter, Kotlin, Python, and SQL.",
                        fontSize = 12.sp,
                        color = fontColor.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Kotlin", "Flutter", "Python", "SQL").forEach { lang ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(fontColor.copy(alpha = 0.02f), RoundedCornerShape(10.dp))
                                    .border(BorderStroke(1.dp, fontColor.copy(alpha = 0.08f)), RoundedCornerShape(10.dp))
                                    .clickable {
                                        viewModel.sendMessage("Generate a premium standard mock $lang utility algorithm class with explanatory inline parameters.")
                                        viewModel.selectTab(0)
                                    }
                                    .padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = lang,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = fontColor.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }

        // BENTO BOX 5: Job & Resume Match Box
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = fontColor.copy(alpha = 0.03f)),
                border = BorderStroke(1.dp, fontColor.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Job Assistant",
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Job Assistant",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = fontColor
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Track matching indices and trigger rule-based automated job applications.",
                        fontSize = 12.sp,
                        color = fontColor.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    jobApplications.take(2).forEach { job ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(fontColor.copy(alpha = 0.015f), RoundedCornerShape(10.dp))
                                .border(BorderStroke(1.dp, fontColor.copy(alpha = 0.06f)), RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(job.jobTitle, fontWeight = FontWeight.Bold, color = fontColor, fontSize = 13.sp)
                                Text(job.company, fontSize = 11.sp, color = fontColor.copy(alpha = 0.5f))
                            }
                            Box(
                                modifier = Modifier
                                    .background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "${job.matchPercentage}% MATCH",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accentColor,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            viewModel.applyJobAutomated("Senior Android Architect (Kotlin/Compose)", "AI Space Labs")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Jet applications", tint = accentColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "RUN AUTO MATCH ARCHITECT APPLY RULE",
                            color = accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// --- SYSTEM PROFILE & ACCENT SETTINGS TAB ---

@Composable
fun SettingsTabScreen(
    viewModel: MainViewModel,
    settings: UserSettings,
    accentColor: Color,
    fontColor: Color
) {
    var expandPresetMenu by remember { mutableStateOf(false) }
    var textInputWakeWord by remember { mutableStateOf(settings.assistantName) }

    val presetThemesList = listOf("Blue Neon", "White Glow", "Purple Cyber", "Green Matrix", "Red Tech", "Gold Premium")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "SYSTEM CONFIGURATION SYSTEM",
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            color = accentColor,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Text(
            text = "AFIFA Preferences",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = fontColor
        )

        // Custom Assistant name customization card
        Card(
            colors = CardDefaults.cardColors(containerColor = fontColor.copy(alpha = 0.02f)),
            border = BorderStroke(1.dp, fontColor.copy(alpha = 0.08f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "RENAME ASSISTANT & WAKE WORD",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = textInputWakeWord,
                    onValueChange = {
                        textInputWakeWord = it
                        viewModel.updateAssistantName(it)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = accentColor
                    ),
                    textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = fontColor),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Current Wake Word triggers on \"Hi ${settings.assistantName}\"",
                    fontSize = 11.sp,
                    color = fontColor.copy(alpha = 0.5f),
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Custom theme customization card
        Card(
            colors = CardDefaults.cardColors(containerColor = fontColor.copy(alpha = 0.02f)),
            border = BorderStroke(1.dp, fontColor.copy(alpha = 0.08f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "PRESET THEME ACCENTS",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Spacer(modifier = Modifier.height(12.dp))

                Box {
                    Button(
                        onClick = { expandPresetMenu = !expandPresetMenu },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "ACTIVE PRESET: ${settings.themePreset.uppercase()}",
                                color = accentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Dropdown preset selection", tint = accentColor)
                        }
                    }

                    DropdownMenu(
                        expanded = expandPresetMenu,
                        onDismissRequest = { expandPresetMenu = false },
                        modifier = Modifier.background(Color(0xFF1c1c1f))
                    ) {
                        presetThemesList.forEach { preset ->
                            DropdownMenuItem(
                                text = { Text(preset, color = fontColor) },
                                onClick = {
                                    viewModel.selectThemePreset(preset)
                                    expandPresetMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Fingerprint PIN setup simulation triggers
        Card(
            colors = CardDefaults.cardColors(containerColor = fontColor.copy(alpha = 0.02f)),
            border = BorderStroke(1.dp, fontColor.copy(alpha = 0.08f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SECURITY AUTH CONTROL",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Spacer(modifier = Modifier.height(12.dp))

                listOf("PIN Authentication", "Face Unlock Integration", "Biometric Validation", "Voice Identity Auth").forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = type, color = fontColor, fontSize = 13.sp)
                        Switch(
                            checked = settings.securityType.equals(type, ignoreCase = true) || (type.contains("biometric", true) && settings.securityType.contains("biometric", true)),
                            onCheckedChange = {
                                viewModel.updateSecurityType(if (it) type else "NONE")
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = accentColor)
                        )
                    }
                }
            }
        }

        // Reset system loops action
        Button(
            onClick = { viewModel.resetOnboarding() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.12f)),
            border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "RERUN ONBOARDING INSTRUCTION LABS", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}
