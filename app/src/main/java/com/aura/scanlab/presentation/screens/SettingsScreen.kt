package com.aura.scanlab.presentation.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.scanlab.R
import com.aura.scanlab.data.local.PreferenceManager
import com.aura.scanlab.utils.LocaleHelper

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val historyViewModel = remember { HistoryViewModel(context) }
    val preferenceManager = remember { PreferenceManager(context) }
    
    var showClearConfirmation by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.settings_title),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                stringResource(R.string.settings_general),
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
            )

            // Language Item
            val currentLangCode = preferenceManager.getLanguage()
            val currentLangName = PreferenceManager.SUPPORTED_LANGUAGES.find { it.code == currentLangCode }?.displayName ?: "English"
            
            SettingsItem(
                title = stringResource(R.string.language),
                subtitle = currentLangName,
                icon = Icons.Default.Language,
                color = Color(0xFF4285F4), // Google Blue
                onClick = { showLanguageDialog = true }
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            // Clear History Item
            SettingsItem(
                title = stringResource(R.string.clear_history),
                icon = Icons.Default.Delete,
                color = Color(0xFFFF5252),
                onClick = { showClearConfirmation = true }
            )
        }
    }

    // Language Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            containerColor = Color(0xFF1A1A1A),
            title = {
                Text(stringResource(R.string.select_language), color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    PreferenceManager.SUPPORTED_LANGUAGES.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    preferenceManager.setLanguage(lang.code)
                                    LocaleHelper.setLocale(context, lang.code)
                                    showLanguageDialog = false
                                    // Recreation needed to apply language change instantly
                                    (context as? Activity)?.recreate()
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = preferenceManager.getLanguage() == lang.code,
                                onClick = null, // Handled by Row click
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00E676), unselectedColor = Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(lang.displayName, color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            containerColor = Color(0xFF1A1A1A),
            title = {
                Text(stringResource(R.string.clear_history_title), color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    stringResource(R.string.clear_history_message),
                    color = Color.LightGray
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        historyViewModel.clearHistory()
                        showClearConfirmation = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFF5252))
                ) {
                    Text(stringResource(R.string.clear))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearConfirmation = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF141414)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = color,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.DarkGray
            )
        }
    }
}
