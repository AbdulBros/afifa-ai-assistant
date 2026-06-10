package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.data.UserSettings

object ThemeHelper {

    fun getAccentColor(settings: UserSettings): Color {
        return try {
            Color(android.graphics.Color.parseColor(settings.customAccentHex))
        } catch (e: Exception) {
            Color(0xFF00daf3) // Blue neon fallback
        }
    }

    fun getBackgroundColor(settings: UserSettings): Color {
        return try {
            Color(android.graphics.Color.parseColor(settings.customBackgroundHex))
        } catch (e: Exception) {
            Color(0xFF131313) // Deep black void fallback
        }
    }

    fun getFontColor(settings: UserSettings): Color {
        return try {
            Color(android.graphics.Color.parseColor(settings.customFontColorHex))
        } catch (e: Exception) {
            Color(0xFFFFFFFF)
        }
    }

    fun getWaveColor(settings: UserSettings): Color {
        return try {
            Color(android.graphics.Color.parseColor(settings.customWaveColorHex))
        } catch (e: Exception) {
            Color(0xFF00daf3)
        }
    }

    fun getButtonColor(settings: UserSettings): Color {
        return try {
            Color(android.graphics.Color.parseColor(settings.customButtonColorHex))
        } catch (e: Exception) {
            Color(0xFF00daf3)
        }
    }
}
