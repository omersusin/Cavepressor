package com.cavepressor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cavepressor.data.datastore.SettingsDataStore
import com.cavepressor.domain.model.ApiProvider
import com.cavepressor.domain.model.CaveModel
import com.cavepressor.domain.model.CompressionLevel
import com.cavepressor.domain.model.CompressionResult
import com.cavepressor.domain.usecase.CompressTextUseCase
import com.cavepressor.domain.usecase.FetchModelsUseCase
import com.cavepressor.domain.usecase.GetHistoryUseCase
import com.cavepressor.ui.theme.AppThemeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CompressorUiState(
    val inputText: String = "",
    val result: CompressionResult? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val inputTokens: Int = 0
)

data class SettingsUiState(
    val selectedProvider: ApiProvider = ApiProvider.GROQ,
    val selectedModel: String = "llama-3.3-70b-versatile",
    val compressionLevel: CompressionLevel = CompressionLevel.MEDIUM,
    val openRouterKey: String = "",
    val groqKey: String = "",
    val availableModels: List<CaveModel> = emptyList(),
    val isLoadingModels: Boolean = false,
    val appTheme: AppThemeType = AppThemeType.SAGE,
    val darkTheme: Boolean = true,
    val useDynamicColor: Boolean = false,
    val customColor: Int = android.graphics.Color.GREEN
)

@HiltViewModel
class CompressorViewModel @Inject constructor(
    private val compressTextUseCase: CompressTextUseCase,
    private val fetchModelsUseCase: FetchModelsUseCase,
    private val getHistoryUseCase: GetHistoryUseCase,
    private val settings: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompressorUiState())
    val uiState: StateFlow<CompressorUiState> = _uiState.asStateFlow()

    private val _settingsState = MutableStateFlow(SettingsUiState())
    val settingsState: StateFlow<SettingsUiState> = _settingsState.asStateFlow()

    val history = getHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settings.selectedProvider.collect { provider ->
                _settingsState.update { it.copy(selectedProvider = provider) }
            }
        }
        viewModelScope.launch {
            settings.selectedModel.collect { model ->
                _settingsState.update { it.copy(selectedModel = model) }
            }
        }
        viewModelScope.launch {
            settings.compressionLevel.collect { level ->
                _settingsState.update { it.copy(compressionLevel = level) }
            }
        }
        viewModelScope.launch {
            settings.openRouterApiKey.collect { key ->
                _settingsState.update { it.copy(openRouterKey = key) }
            }
        }
        viewModelScope.launch {
            settings.groqApiKey.collect { key ->
                _settingsState.update { it.copy(groqKey = key) }
            }
        }
        viewModelScope.launch {
            settings.customColor.collect { color ->
                _settingsState.update { it.copy(customColor = color) }
            }
        }
        viewModelScope.launch {
            settings.appTheme.collect { theme ->
                _settingsState.update { it.copy(appTheme = theme) }
            }
        }
        viewModelScope.launch {
            settings.darkTheme.collect { dark ->
                _settingsState.update { it.copy(darkTheme = dark) }
            }
        }
        viewModelScope.launch {
            settings.useDynamicColor.collect { dynamic ->
                _settingsState.update { it.copy(useDynamicColor = dynamic) }
            }
        }
    }

    fun onInputChange(text: String) {
        val tokens = compressTextUseCase.estimateTokens(text)
        _uiState.update { it.copy(inputText = text, inputTokens = tokens, error = null) }
    }

    fun compress() {
        val text = _uiState.value.inputText
        if (text.isBlank()) {
            _uiState.update { it.copy(error = "Please enter some text to compress") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, result = null) }
            val result = compressTextUseCase(text)
            result.fold(
                onSuccess = { compression ->
                    _uiState.update { it.copy(isLoading = false, result = compression) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    fun clearResult() {
        _uiState.update { it.copy(result = null, error = null) }
    }

    fun clearInput() {
        _uiState.update { it.copy(inputText = "", inputTokens = 0, result = null, error = null) }
    }

    fun setProvider(provider: ApiProvider) {
        viewModelScope.launch {
            settings.setSelectedProvider(provider)
            fetchModels(provider)
        }
    }

    fun setModel(model: String) {
        viewModelScope.launch { settings.setSelectedModel(model) }
    }

    fun setCompressionLevel(level: CompressionLevel) {
        viewModelScope.launch { settings.setCompressionLevel(level) }
    }

    fun setOpenRouterKey(key: String) {
        viewModelScope.launch { settings.setOpenRouterApiKey(key) }
    }

    fun setGroqKey(key: String) {
        viewModelScope.launch { settings.setGroqApiKey(key) }
    }

    fun setCustomColor(color: Int) {
        viewModelScope.launch { settings.setCustomColor(color) }
    }

    fun setAppTheme(theme: AppThemeType) {
        viewModelScope.launch { settings.setAppTheme(theme) }
    }

    fun setDarkTheme(dark: Boolean) {
        viewModelScope.launch { settings.setDarkTheme(dark) }
    }

    fun setDynamicColor(dynamic: Boolean) {
        viewModelScope.launch { settings.setUseDynamicColor(dynamic) }
    }

    fun fetchModels(provider: ApiProvider? = null) {
        val targetProvider = provider ?: _settingsState.value.selectedProvider
        viewModelScope.launch {
            _settingsState.update { it.copy(isLoadingModels = true) }
            val result = fetchModelsUseCase(targetProvider)
            result.fold(
                onSuccess = { models ->
                    _settingsState.update { it.copy(availableModels = models, isLoadingModels = false) }
                },
                onFailure = {
                    _settingsState.update { it.copy(isLoadingModels = false) }
                }
            )
        }
    }

    fun deleteHistory(id: Long) {
        viewModelScope.launch { getHistoryUseCase.deleteById(id) }
    }

    fun clearHistory() {
        viewModelScope.launch { getHistoryUseCase.clearAll() }
    }
}
