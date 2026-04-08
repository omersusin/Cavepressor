package com.omersusin.cavepressor.ui.screens

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omersusin.cavepressor.domain.model.ApiProvider
import com.omersusin.cavepressor.domain.model.AppTheme
import com.omersusin.cavepressor.ui.components.ApiKeyDialog
import com.omersusin.cavepressor.ui.components.ModelSelector
import com.omersusin.cavepressor.viewmodel.CompressorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: CompressorViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val settings by viewModel.settingsState.collectAsState()
    var showOpenRouterDialog by remember { mutableStateOf(false) }
    var showGroqDialog by remember { mutableStateOf(false) }
    var customModelInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(settings.selectedProvider) {
        viewModel.fetchModels(settings.selectedProvider)
    }

    if (showOpenRouterDialog) {
        ApiKeyDialog(
            providerName = "OpenRouter",
            currentKey = settings.openRouterKey,
            onConfirm = { viewModel.setOpenRouterKey(it); showOpenRouterDialog = false },
            onDismiss = { showOpenRouterDialog = false }
        )
    }
    if (showGroqDialog) {
        ApiKeyDialog(
            providerName = "Groq",
            currentKey = settings.groqKey,
            onConfirm = { viewModel.setGroqKey(it); showGroqDialog = false },
            onDismiss = { showGroqDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Appearance ────────────────────────────────────────────────
            item {
                SettingsSectionCard(title = "Appearance", icon = Icons.Default.Palette) {

                    // Dark Mode
                    SettingsToggleRow(
                        title = "Dark Mode",
                        subtitle = "Dark background, light text",
                        icon = Icons.Default.DarkMode,
                        checked = settings.darkTheme,
                        onCheckedChange = { viewModel.setDarkTheme(it) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // AMOLED Mode
                    SettingsToggleRow(
                        title = "AMOLED Mode",
                        subtitle = "Pure black — saves battery on OLED screens",
                        icon = Icons.Default.Star,
                        checked = settings.amoledMode,
                        enabled = settings.darkTheme,
                        onCheckedChange = { viewModel.setAmoledMode(it) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dynamic Color
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        SettingsToggleRow(
                            title = "Dynamic Color",
                            subtitle = "Uses your wallpaper colors (Android 12+)",
                            icon = Icons.Default.Palette,
                            checked = settings.useDynamicColor,
                            onCheckedChange = { viewModel.setDynamicColor(it) }
                        )
                        if (settings.useDynamicColor) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "⚡ Restart app to fully apply",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            // ── API Provider ──────────────────────────────────────────────
            item {
                SettingsSectionCard(title = "API Provider", icon = Icons.Default.SmartToy) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ApiProvider.entries.forEach { provider ->
                            FilterChip(
                                selected = settings.selectedProvider == provider,
                                onClick = { viewModel.setProvider(provider) },
                                label = { Text(provider.displayName) },
                                leadingIcon = if (settings.selectedProvider == provider) {
                                    { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
                                } else null,
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "API Keys",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ApiKeyRow(
                        providerName = "OpenRouter",
                        hasKey = settings.openRouterKey.isNotBlank(),
                        onClick = { showOpenRouterDialog = true }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ApiKeyRow(
                        providerName = "Groq",
                        hasKey = settings.groqKey.isNotBlank(),
                        onClick = { showGroqDialog = true }
                    )
                }
            }

            // ── Model ─────────────────────────────────────────────────────
            item {
                SettingsSectionCard(title = "Model", icon = Icons.Default.SmartToy) {
                    if (settings.availableModels.isNotEmpty()) {
                        Text(
                            "Select from list",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        ModelSelector(
                            models = settings.availableModels,
                            selectedModelId = settings.selectedModel,
                            onModelSelected = { viewModel.setModel(it) }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    Text(
                        "Or enter model ID manually",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customModelInput,
                            onValueChange = { customModelInput = it },
                            modifier = Modifier.weight(1f),
                            placeholder = {
                                Text(
                                    text = if (settings.selectedProvider == ApiProvider.GROQ)
                                        "llama-3.3-70b-versatile"
                                    else "openai/gpt-4o-mini",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            textStyle = MaterialTheme.typography.bodySmall,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                if (customModelInput.isNotBlank()) {
                                    viewModel.setModel(customModelInput.trim())
                                    customModelInput = ""
                                    focusManager.clearFocus()
                                }
                            })
                        )
                        IconButton(onClick = {
                            if (customModelInput.isNotBlank()) {
                                viewModel.setModel(customModelInput.trim())
                                customModelInput = ""
                                focusManager.clearFocus()
                            }
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Apply", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    if (settings.selectedModel.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit, null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Active: ${settings.selectedModel}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // ── About ─────────────────────────────────────────────────────
            item {
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "🪨 Cavepressor",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Semantic compression for LLM contexts.\nBased on Caveman Compression by William Peltomäki.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("v1.0.0", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}



private fun Color.compositeOver(background: Color): Color {
    val a = this.alpha
    return Color(
        red = this.red * a + background.red * (1 - a),
        green = this.green * a + background.green * (1 - a),
        blue = this.blue * a + background.blue * (1 - a),
        alpha = 1f
    )
}

@Composable
private fun SettingsSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(icon, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun ApiKeyRow(
    providerName: String,
    hasKey: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(providerName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
            Text(
                text = if (hasKey) "Key configured ✓" else "No key set",
                style = MaterialTheme.typography.labelSmall,
                color = if (hasKey) MaterialTheme.colorScheme.tertiary
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
        OutlinedButton(onClick = onClick, shape = MaterialTheme.shapes.medium) {
            Icon(Icons.Default.Key, null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.size(4.dp))
            Text(if (hasKey) "Update" else "Set Key", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                icon, null,
                modifier = Modifier.size(20.dp),
                tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = if (enabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = if (enabled) 0.6f else 0.3f
                    )
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}
