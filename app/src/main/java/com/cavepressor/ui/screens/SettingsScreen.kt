package com.cavepressor.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SmartToy
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cavepressor.domain.model.ApiProvider
import com.cavepressor.ui.components.ApiKeyDialog
import com.cavepressor.ui.components.ModelSelector
import com.cavepressor.ui.components.ThemeSelectorGrid
import com.cavepressor.ui.theme.AppThemeType
import com.cavepressor.data.datastore.EngineType
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border

import com.cavepressor.viewmodel.CompressorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: CompressorViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val settings by viewModel.settingsState.collectAsState()
    var showOpenRouterDialog by remember { mutableStateOf(false) }
    var showGroqDialog by remember { mutableStateOf(false) }
    var showHfDialog by remember { mutableStateOf(false) }
    var customModelInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var showColorPicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.checkLocalModelStatus("gemma-2b-it-cpu-int4.bin")
    }

    LaunchedEffect(settings.selectedProvider) {
        viewModel.fetchModels(settings.selectedProvider)
    }

    if (showOpenRouterDialog) {
        ApiKeyDialog(
            providerName = "OpenRouter",
            currentKey = settings.openRouterKey,
            onConfirm = {
                viewModel.setOpenRouterKey(it)
                showOpenRouterDialog = false
            },
            onDismiss = { showOpenRouterDialog = false }
        )
    }

if (showColorPicker) {
        com.cavepressor.ui.components.CustomColorDialog(
            initialColor = androidx.compose.ui.graphics.Color(settings.customColor),
            onColorSelected = { color ->
                viewModel.setCustomColor(color.toArgb())
                viewModel.setAppTheme(AppThemeType.CUSTOM)
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false }
        )
    }

    if (showHfDialog) {
        ApiKeyDialog(
            providerName = "Hugging Face",
            currentKey = settings.huggingFaceKey,
            onConfirm = {
                viewModel.setHuggingFaceKey(it)
                showHfDialog = false
            },
            onDismiss = { showHfDialog = false }
        )
    }

    if (showGroqDialog) {
        ApiKeyDialog(
            providerName = "Groq",
            currentKey = settings.groqKey,
            onConfirm = {
                viewModel.setGroqKey(it)
                showGroqDialog = false
            },
            onDismiss = { showGroqDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
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


            // AI Engine Type seçimi
            item {
                SettingsSectionCard(title = "AI Engine Type", icon = Icons.Default.Memory) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EngineType.entries.filter { it != EngineType.NLP }.forEach { type ->
                            val displayName = when (type) {
                                EngineType.CLOUD_LLM -> "Cloud API"
                                EngineType.LOCAL_LLM -> "Local Phone"
                                else -> "Basic NLP"
                            }
                            val displayIcon = when (type) {
                                EngineType.CLOUD_LLM -> Icons.Default.Cloud
                                EngineType.LOCAL_LLM -> Icons.Default.PhoneAndroid
                                else -> Icons.Default.Memory
                            }
                            FilterChip(
                                selected = settings.engineType == type,
                                onClick = { viewModel.setEngineType(type) },
                                label = { Text(displayName) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = displayIcon,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                    
                    if (settings.engineType == EngineType.LOCAL_LLM) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Experimental: Runs an AI model locally using your phone's memory. No internet required.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                Text("Gemma 2B (INT4 CPU)", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("~1.4 GB model file from Google", style = MaterialTheme.typography.labelSmall)
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                val dlState = settings.localModelDownloadState
                                when (dlState) {
                                    is com.cavepressor.domain.usecase.DownloadState.Idle -> {
                                        androidx.compose.material3.OutlinedButton(
                                            onClick = {
                                                viewModel.downloadLocalModel(
                                                    "https://storage.googleapis.com/mediapipe-tasks/genai/gemma-2b-it-cpu-int4.bin",
                                                    "gemma-2b-it-cpu-int4.bin"
                                                )
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Download Model")
                                        }
                                    }
                                    is com.cavepressor.domain.usecase.DownloadState.Downloading -> {
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            Text("Downloading... ${dlState.progress}%", style = MaterialTheme.typography.labelSmall)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            androidx.compose.material3.LinearProgressIndicator(
                                                progress = { dlState.progress / 100f },
                                                modifier = Modifier.fillMaxWidth().height(6.dp),
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                    is com.cavepressor.domain.usecase.DownloadState.Success -> {
                                        Text(
                                            "✓ Downloaded & Ready to Compress",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    is com.cavepressor.domain.usecase.DownloadState.Error -> {
                                        Text(
                                            "Error: ${dlState.message}",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        androidx.compose.material3.OutlinedButton(
                                            onClick = {
                                                viewModel.downloadLocalModel(
                                                    "https://storage.googleapis.com/mediapipe-tasks/genai/gemma-2b-it-cpu-int4.bin",
                                                    "gemma-2b-it-cpu-int4.bin"
                                                )
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Retry Download")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (settings.engineType == EngineType.CLOUD_LLM) {
            // Provider seçimi
            item {
                SettingsSectionCard(title = "API Provider", icon = Icons.Default.SmartToy) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ApiProvider.entries.forEach { provider ->
                            val providerIcon = when (provider) {
                                ApiProvider.GROQ -> com.cavepressor.R.drawable.ic_groq
                                ApiProvider.OPENROUTER -> com.cavepressor.R.drawable.ic_openrouter
                                ApiProvider.HUGGING_FACE -> com.cavepressor.R.drawable.ic_huggingface
                            }
                            FilterChip(
                                selected = settings.selectedProvider == provider,
                                onClick = { viewModel.setProvider(provider) },
                                label = { Text(provider.displayName) },
                                leadingIcon = {
                                    if (settings.selectedProvider == provider) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Icon(
                                            painter = androidx.compose.ui.res.painterResource(id = providerIcon),
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                },
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
                        text = "API Keys",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ApiKeyRow(
                        providerName = "OpenRouter",
                        hasKey = settings.openRouterKey.isNotBlank(),
                        iconRes = com.cavepressor.R.drawable.ic_openrouter,
                        onClick = { showOpenRouterDialog = true }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ApiKeyRow(
                        providerName = "Groq",
                        hasKey = settings.groqKey.isNotBlank(),
                        iconRes = com.cavepressor.R.drawable.ic_groq,
                        onClick = { showGroqDialog = true }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ApiKeyRow(
                        providerName = "Hugging Face",
                        hasKey = settings.huggingFaceKey.isNotBlank(),
                        iconRes = com.cavepressor.R.drawable.ic_huggingface,
                        onClick = { showHfDialog = true }
                    )
                    
                    if (settings.selectedProvider == ApiProvider.HUGGING_FACE) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "Tip: You can use free serverless inference on Hugging Face! Get your free Access Token from huggingface.co/settings/tokens.\nNote: Free tier models might sleep and need a few seconds to wake up.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            // Model seçimi
            item {
                SettingsSectionCard(title = "Model", icon = Icons.Default.SmartToy) {

                    // Dropdown listeden seç
                    if (settings.availableModels.isNotEmpty()) {
                        Text(
                            text = "Select from list",
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

                    // Manuel model girişi
                    Text(
                        text = "Or enter model ID manually",
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
                                    text = when (settings.selectedProvider) {
                                        ApiProvider.GROQ -> "llama-3.3-70b-versatile"
                                        ApiProvider.OPENROUTER -> "openai/gpt-4o-mini"
                                        ApiProvider.HUGGING_FACE -> "meta-llama/Llama-3.2-3B-Instruct"
                                    },
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
                        IconButton(
                            onClick = {
                                if (customModelInput.isNotBlank()) {
                                    viewModel.setModel(customModelInput.trim())
                                    customModelInput = ""
                                    focusManager.clearFocus()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Apply",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Seçili model göster
                    if (settings.selectedModel.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Active: ${settings.selectedModel}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            }

            // Görünüm
            item {
                SettingsSectionCard(title = "Appearance", icon = Icons.Default.Palette) {
                    SettingsToggleRow(
                        title = "Dark Theme",
                        subtitle = "Cave-optimized dark palette",
                        icon = Icons.Default.DarkMode,
                        checked = settings.darkTheme,
                        onCheckedChange = { viewModel.setDarkTheme(it) }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    ThemeSelectorGrid(
                        selectedTheme = settings.appTheme,
                        customColor = androidx.compose.ui.graphics.Color(settings.customColor),
                        onThemeSelected = { viewModel.setAppTheme(it) },
                        onCustomThemeEdit = { showColorPicker = true }
                    )
                }
            }

            // Hakkında
            item {
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🪨 Cavepressor",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Semantic compression for LLM contexts.\nBased on Caveman Compression by William Peltomäki.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "v1.0.0",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = title,
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
    iconRes: Int? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (iconRes != null) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = iconRes),
                    contentDescription = providerName,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Column {
                Text(
                    text = providerName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
                Text(
                    text = if (hasKey) "Key configured ✓" else "No key set",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (hasKey) MaterialTheme.colorScheme.tertiary
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
        OutlinedButton(
            onClick = onClick
        ) {
            Icon(
                imageVector = Icons.Default.Key,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = if (hasKey) "Update" else "Set Key",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
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
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
