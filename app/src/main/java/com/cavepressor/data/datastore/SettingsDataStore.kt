package com.cavepressor.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cavepressor.domain.model.ApiProvider
import com.cavepressor.domain.model.CompressionLevel
import com.cavepressor.ui.theme.AppThemeType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cavepressor_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val KEY_OPENROUTER_API_KEY = stringPreferencesKey("openrouter_api_key")
        val KEY_GROQ_API_KEY = stringPreferencesKey("groq_api_key")
        val KEY_SELECTED_PROVIDER = stringPreferencesKey("selected_provider")
        val KEY_SELECTED_MODEL = stringPreferencesKey("selected_model")
        val KEY_COMPRESSION_LEVEL = stringPreferencesKey("compression_level")
        val KEY_APP_THEME = stringPreferencesKey("app_theme")
        val KEY_DARK_THEME = booleanPreferencesKey("dark_theme")
        val KEY_USE_DYNAMIC_COLOR = booleanPreferencesKey("use_dynamic_color")
        val KEY_CUSTOM_COLOR = intPreferencesKey("custom_color")
    }

    val openRouterApiKey: Flow<String> = context.dataStore.data
        .map { it[KEY_OPENROUTER_API_KEY] ?: "" }

    val groqApiKey: Flow<String> = context.dataStore.data
        .map { it[KEY_GROQ_API_KEY] ?: "" }

    val selectedProvider: Flow<ApiProvider> = context.dataStore.data
        .map {
            val name = it[KEY_SELECTED_PROVIDER] ?: ApiProvider.GROQ.name
            ApiProvider.valueOf(name)
        }

    val selectedModel: Flow<String> = context.dataStore.data
        .map { it[KEY_SELECTED_MODEL] ?: "llama-3.3-70b-versatile" }

    val compressionLevel: Flow<CompressionLevel> = context.dataStore.data
        .map {
            val name = it[KEY_COMPRESSION_LEVEL] ?: CompressionLevel.MEDIUM.name
            CompressionLevel.valueOf(name)
        }

    val appTheme: Flow<AppThemeType> = context.dataStore.data
        .map {
            val name = it[KEY_APP_THEME] ?: AppThemeType.SAGE.name
            AppThemeType.valueOf(name)
        }

    val darkTheme: Flow<Boolean> = context.dataStore.data
        .map { it[KEY_DARK_THEME] ?: true }

    val customColor: Flow<Int> = context.dataStore.data
        .map { it[KEY_CUSTOM_COLOR] ?: android.graphics.Color.GREEN }

    val useDynamicColor: Flow<Boolean> = context.dataStore.data
        .map { it[KEY_USE_DYNAMIC_COLOR] ?: false }

    suspend fun setOpenRouterApiKey(key: String) {
        context.dataStore.edit { it[KEY_OPENROUTER_API_KEY] = key }
    }

    suspend fun setGroqApiKey(key: String) {
        context.dataStore.edit { it[KEY_GROQ_API_KEY] = key }
    }

    suspend fun setSelectedProvider(provider: ApiProvider) {
        context.dataStore.edit { it[KEY_SELECTED_PROVIDER] = provider.name }
    }

    suspend fun setSelectedModel(model: String) {
        context.dataStore.edit { it[KEY_SELECTED_MODEL] = model }
    }

    suspend fun setCompressionLevel(level: CompressionLevel) {
        context.dataStore.edit { it[KEY_COMPRESSION_LEVEL] = level.name }
    }

    suspend fun setAppTheme(theme: AppThemeType) {
        context.dataStore.edit { it[KEY_APP_THEME] = theme.name }
    }

    suspend fun setCustomColor(color: Int) {
        context.dataStore.edit { it[KEY_CUSTOM_COLOR] = color }
    }

    suspend fun setDarkTheme(dark: Boolean) {
        context.dataStore.edit { it[KEY_DARK_THEME] = dark }
    }

    suspend fun setUseDynamicColor(dynamic: Boolean) {
        context.dataStore.edit { it[KEY_USE_DYNAMIC_COLOR] = dynamic }
    }
}
