package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserSettings
import com.example.ui.theme.ThemeHelper
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentStep by viewModel.onboardingStep.collectAsState()
    val settings by viewModel.settingsState.collectAsState()
    val permissions by viewModel.permissionsMap.collectAsState()

    val accentColor = ThemeHelper.getAccentColor(settings)
    val backgroundColor = ThemeHelper.getBackgroundColor(settings)
    val fontColor = ThemeHelper.getFontColor(settings)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Ambient background glowing spots
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(80.dp)
                .alpha(0.15f)
        ) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.TopEnd)
                    .background(accentColor, CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .align(Alignment.BottomStart)
                    .background(accentColor.copy(alpha = 0.5f), CircleShape)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Top Brand
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "AFIFA",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    letterSpacing = 6.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "YOUR VOICE · YOUR WORLD",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = fontColor.copy(alpha = 0.4f),
                    letterSpacing = 3.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Onboarding Step Content Box (with animated slide transitions)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> -width } + fadeOut()
                            )
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> width } + fadeOut()
                            )
                        }
                    },
                    label = "OnboardingContent"
                ) { step ->
                    when (step) {
                        1 -> SplashStep(viewModel, settings, accentColor, fontColor)
                        2 -> WelcomeStep(viewModel, settings, accentColor, fontColor)
                        3 -> UserNameStep(viewModel, settings, accentColor, fontColor)
                        4 -> AiNameStep(viewModel, settings, accentColor, fontColor)
                        5 -> VoiceSelectionStep(viewModel, settings, accentColor, fontColor)
                        6 -> VoiceVerificationStep(viewModel, settings, accentColor, fontColor)
                        7 -> VoiceCloneStep(viewModel, settings, accentColor, fontColor)
                        8 -> PermissionsStep(viewModel, permissions, accentColor, fontColor)
                        9 -> SetupCompleteStep(viewModel, settings, accentColor, fontColor)
                    }
                }
            }

            // Bottom Navigation Indicators & Buttons (Suppress for Splash)
            if (currentStep > 1) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Progression dots indicator
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        for (i in 2..9) {
                            val isActive = i == currentStep
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .height(5.dp)
                                    .width(if (isActive) 24.dp else 8.dp)
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(if (isActive) accentColor else fontColor.copy(alpha = 0.2f))
                            )
                        }
                    }

                    // Next / Back Actions Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.prevOnboardingStep() },
                            modifier = Modifier
                                .border(1.dp, fontColor.copy(alpha = 0.15f), CircleShape)
                                .size(50.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Go Back",
                                tint = fontColor.copy(alpha = 0.8f)
                            )
                        }

                        Button(
                            onClick = { viewModel.nextOnboardingStep() },
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                                .height(56.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (currentStep == 9) "INITIALIZE CORE" else "CONTINUE",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 15.sp,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Forward step",
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- SPECIFIC ONBOARDING STEPS IMPLEMENTATION ---

@Composable
fun SplashStep(
    viewModel: MainViewModel,
    settings: UserSettings,
    accentColor: Color,
    fontColor: Color
) {
    var technicalText by remember { mutableStateOf("establishing_neural_link...") }
    val progressAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        val technicalStatuses = listOf(
            "establishing_neural_link...",
            "optimizing_context_engines...",
            "calibrating_voice_harmonics...",
            "loading_premium_assets...",
            "encryption_handshake_success...",
            "afifa_ready_for_input..."
        )

        launch {
            progressAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 4000, easing = LinearEasing)
            )
        }

        for (status in technicalStatuses) {
            technicalText = status
            delay(800)
        }

        // Auto transition to welcome step
        viewModel.nextOnboardingStep()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        // Hologram glowing container
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(200.dp)
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val scalePulse by infiniteTransition.animateFloat(
                initialValue = 0.95f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseLogo"
            )

            // Dynamic background glow
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scalePulse)
                    .background(accentColor.copy(alpha = 0.08f), CircleShape)
                    .border(2.dp, accentColor.copy(alpha = 0.2f), CircleShape)
            )

            // Wave simulator inside
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Hologram voice wave",
                tint = accentColor,
                modifier = Modifier
                    .size(90.dp)
                    .scale(scalePulse)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Loading bar
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(3.dp)
                .background(fontColor.copy(alpha = 0.1f), RoundedCornerShape(100.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progressAnim.value)
                    .background(accentColor, RoundedCornerShape(100.dp))
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "INITIALIZING CORE",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = fontColor.copy(alpha = 0.5f),
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = technicalText,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            color = accentColor.copy(alpha = 0.8f),
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WelcomeStep(
    viewModel: MainViewModel,
    settings: UserSettings,
    accentColor: Color,
    fontColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Welcome Icon",
            tint = accentColor,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Your Voice. Your Command.\nYour World.",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = fontColor,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp,
            fontFamily = FontFamily.SansSerif
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "AFIFA is your hyper-intelligent personal companion, productivity cowriting partner, and automation pilot.",
            fontSize = 15.sp,
            color = fontColor.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = fontColor.copy(alpha = 0.03f)),
            border = BorderStroke(1.dp, fontColor.copy(alpha = 0.08f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Security Alert",
                    tint = accentColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Secure local vault environment configured. Your personal identity models are secure.",
                    fontSize = 13.sp,
                    color = fontColor.copy(alpha = 0.7f),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun UserNameStep(
    viewModel: MainViewModel,
    settings: UserSettings,
    accentColor: Color,
    fontColor: Color
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var textInput by remember { mutableStateOf(settings.userName) }

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "STEP 03: PROFILE",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = accentColor,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "What should I call you?",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = fontColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "I will calibrate my responses to address you personally during secure intelligence loops.",
            fontSize = 15.sp,
            color = fontColor.copy(alpha = 0.6f),
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = textInput,
            onValueChange = {
                textInput = it
                viewModel.updateUserName(it)
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = fontColor,
                unfocusedTextColor = fontColor,
                focusedIndicatorColor = accentColor,
                unfocusedIndicatorColor = fontColor.copy(alpha = 0.2f)
            ),
            textStyle = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = fontColor
            ),
            placeholder = {
                Text(
                    text = "Enter your name...",
                    fontSize = 24.sp,
                    color = fontColor.copy(alpha = 0.3f)
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                capitalization = KeyboardCapitalization.Words
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AiNameStep(
    viewModel: MainViewModel,
    settings: UserSettings,
    accentColor: Color,
    fontColor: Color
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var textInput by remember { mutableStateOf(settings.assistantName) }

    val suggestedNames = listOf("Afifa", "Nova", "Luna", "Zara", "Milo")

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "STEP 04: IDENTITY",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = accentColor,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "What would you like to call your AI assistant?",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = fontColor,
            lineHeight = 36.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "The name you choose will become your assistant's name and wake word.",
            fontSize = 15.sp,
            color = fontColor.copy(alpha = 0.6f),
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = textInput,
            onValueChange = {
                textInput = it
                viewModel.updateAssistantName(it)
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = fontColor,
                unfocusedTextColor = fontColor,
                focusedIndicatorColor = accentColor,
                unfocusedIndicatorColor = fontColor.copy(alpha = 0.2f)
            ),
            textStyle = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = fontColor
            ),
            placeholder = {
                Text(
                    text = "AFIFA",
                    fontSize = 24.sp,
                    color = fontColor.copy(alpha = 0.3f)
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                capitalization = KeyboardCapitalization.Words
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "SUGGESTED NAMES",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = fontColor.copy(alpha = 0.4f),
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Grid of chip name suggestions
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(suggestedNames) { name ->
                val isSelected = textInput.equals(name, ignoreCase = true)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) accentColor.copy(alpha = 0.15f) else fontColor.copy(alpha = 0.03f))
                        .border(
                            1.dp,
                            if (isSelected) accentColor else fontColor.copy(alpha = 0.08f),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            textInput = name
                            viewModel.updateAssistantName(name)
                        }
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = name,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) accentColor else fontColor.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun VoiceSelectionStep(
    viewModel: MainViewModel,
    settings: UserSettings,
    accentColor: Color,
    fontColor: Color
) {
    val voices = listOf("Female (Kore)", "Male (Atlas)", "Cloned (Custom Resonance)")
    var isPlayingPreview by remember { mutableStateOf(false) }

    LaunchedEffect(isPlayingPreview) {
        if (isPlayingPreview) {
            delay(1500)
            isPlayingPreview = false
        }
    }

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "STEP 05: COGNITION VOICE",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = accentColor,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Choose voice profile",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = fontColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Select a prebuilt voice frequency or clone your own signature pattern later.",
            fontSize = 15.sp,
            color = fontColor.copy(alpha = 0.6f),
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            voices.forEach { voice ->
                val isSelected = settings.selectedVoice.equals(voice, ignoreCase = true) ||
                        (voice.contains("female", true) && settings.selectedVoice.contains("female", true)) ||
                        (voice.contains("male", true) && settings.selectedVoice.contains("male", true))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) accentColor.copy(alpha = 0.08f) else fontColor.copy(alpha = 0.02f))
                        .border(
                            1.dp,
                            if (isSelected) accentColor.copy(alpha = 0.4f) else fontColor.copy(alpha = 0.08f),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { viewModel.updateSelectedVoice(voice) }
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.updateSelectedVoice(voice) },
                            colors = RadioButtonDefaults.colors(selectedColor = accentColor)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = voice,
                            color = fontColor,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    }

                    // Sound preview speaker trigger
                    IconButton(
                        onClick = { isPlayingPreview = true }
                    ) {
                        Icon(
                            imageVector = if (isPlayingPreview && isSelected) Icons.Default.PlayArrow else Icons.Default.PlayArrow,
                            contentDescription = "Voice preview",
                            tint = if (isSelected) accentColor else fontColor.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceVerificationStep(
    viewModel: MainViewModel,
    settings: UserSettings,
    accentColor: Color,
    fontColor: Color
) {
    val stepPhase = remember { mutableStateOf(1) } // 3 test phrases
    var isRecordingUser by remember { mutableStateOf(false) }

    val phrasesText = listOf(
        "\"Hi ${settings.assistantName}, synchronize my local memory workspace now.\"",
        "\"Accept system authentication parameters for AFIFA interface control.\"",
        "\"Configure premium responsive dashboard themes and launch workflows.\""
    )

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "STEP 06: SPEAKER SECURITY (Phase ${stepPhase.value}/3)",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = accentColor,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Calibrating Wake Word",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = fontColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Read the phrase aloud below so AFIFA can register your specific vocal harmonics and sign keys.",
            fontSize = 15.sp,
            color = fontColor.copy(alpha = 0.6f),
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Text card containing phrase
        Card(
            colors = CardDefaults.cardColors(containerColor = fontColor.copy(alpha = 0.03f)),
            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = phrasesText[stepPhase.value - 1],
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Recording Wave Indicator Action
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = {
                        if (!isRecordingUser) {
                            isRecordingUser = true
                        } else {
                            isRecordingUser = false
                            if (stepPhase.value < 3) {
                                stepPhase.value += 1
                            } else {
                                viewModel.nextOnboardingStep()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecordingUser) Color.Red else accentColor
                    ),
                    shape = CircleShape,
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        imageVector = if (isRecordingUser) Icons.Default.Close else Icons.Default.PlayArrow,
                        contentDescription = "Voice enrollment",
                        tint = if (isRecordingUser) Color.White else Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isRecordingUser) "LISTENING FREQUENCY..." else "PRESS TO RECORD PHRASE",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = if (isRecordingUser) Color.Red else fontColor.copy(alpha = 0.5f),
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun VoiceCloneStep(
    viewModel: MainViewModel,
    settings: UserSettings,
    accentColor: Color,
    fontColor: Color
) {
    var isRecordingCloning by remember { mutableStateOf(false) }
    var cloneStrength by remember { mutableStateOf(0f) }

    LaunchedEffect(isRecordingCloning) {
        if (isRecordingCloning) {
            while (cloneStrength < 1f) {
                delay(300)
                cloneStrength += 0.1f
            }
            delay(500)
            isRecordingCloning = false
            viewModel.updateSelectedVoice("Cloned (Custom Resonance)")
            viewModel.nextOnboardingStep()
        }
    }

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "STEP 07: VOICE CLONE (OPTIONAL)",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = accentColor,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Generate cloned voice",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = fontColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Record a 5-second technical paragraph to train the synthesis network. If skipped, AFIFA will use standard high-fidelity voice packages.",
            fontSize = 15.sp,
            color = fontColor.copy(alpha = 0.6f),
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isRecordingCloning) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TRAINING SYNTHESIS NETWORK: ${(cloneStrength * 100).toInt()}%",
                    fontFamily = FontFamily.Monospace,
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { cloneStrength },
                    color = accentColor,
                    trackColor = fontColor.copy(alpha = 0.1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(100.dp))
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.nextOnboardingStep() },
                    border = BorderStroke(1.dp, fontColor.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text(
                        text = "SKIP THIS STEP",
                        color = fontColor.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { isRecordingCloning = true },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(56.dp)
                ) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Clone Training")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "START TRAINING",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PermissionsStep(
    viewModel: MainViewModel,
    permissions: Map<String, Boolean>,
    accentColor: Color,
    fontColor: Color
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "STEP 08: INTEGRATIONS",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = accentColor,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Request Permissions",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = fontColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Grant active link permissions to access contacts, receive automated alerts, and capture responsive commands.",
            fontSize = 14.sp,
            color = fontColor.copy(alpha = 0.6f),
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        val descMap = mapOf(
            "Microphone" to "Used for wake word processing and speech input",
            "Storage" to "Used to cache locally encrypted data vector models",
            "Contacts" to "Allows searching client indices during direct calls",
            "Notifications" to "Enables workflow updates and automation triggers",
            "Accessibility Services" to "Enables deep App Automations (WhatsApp, SMS, etc.)"
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            permissions.forEach { (perm, isGranted) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(fontColor.copy(alpha = 0.02f))
                        .border(BorderStroke(1.dp, fontColor.copy(alpha = 0.08f)), RoundedCornerShape(12.dp))
                        .clickable { viewModel.togglePermission(perm) }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = perm,
                            fontWeight = FontWeight.Bold,
                            color = fontColor,
                            fontSize = 14.sp
                        )
                        Text(
                            text = descMap[perm] ?: "",
                            fontSize = 11.sp,
                            color = fontColor.copy(alpha = 0.5f),
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Switch(
                        checked = isGranted,
                        onCheckedChange = { viewModel.togglePermission(perm) },
                        colors = SwitchDefaults.colors(checkedThumbColor = accentColor)
                    )
                }
            }
        }
    }
}

@Composable
fun SetupCompleteStep(
    viewModel: MainViewModel,
    settings: UserSettings,
    accentColor: Color,
    fontColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .background(accentColor.copy(alpha = 0.1f), CircleShape)
                .border(2.dp, accentColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success tick",
                tint = accentColor,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Setup Completed",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = fontColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Welcome onboard! AFIFA has successfully loaded your customized settings.",
            fontSize = 15.sp,
            color = fontColor.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(fontColor.copy(alpha = 0.03f))
                .border(BorderStroke(1.dp, fontColor.copy(alpha = 0.08f)), RoundedCornerShape(16.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "ASSISTANT NAME:", fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = fontColor.copy(0.5f))
                Text(text = settings.assistantName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = accentColor)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "PRIMARY WAKE WORD:", fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = fontColor.copy(0.5f))
                Text(text = "\"Hi ${settings.assistantName}\"", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = accentColor)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "VOICE PRESET:", fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = fontColor.copy(0.5f))
                Text(text = settings.selectedVoice, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = fontColor)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "COGNITIVE MODEL:", fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = fontColor.copy(0.5f))
                Text(text = settings.activeModel, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = fontColor)
            }
        }
    }
}
