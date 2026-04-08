package com.omersusin.cavepressor.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.omersusin.cavepressor.domain.model.ApiProvider
import com.omersusin.cavepressor.domain.model.AppTheme
import com.omersusin.cavepressor.domain.model.CompressionLevel
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
        val KEY_DARK_THEME = booleanPreferencesKey("dark_theme")
        val KEY_AMOLED_MODE = booleanPreferencesKey("amoled_mode")
        val KEY_DYNAMIC_COLOR = booleanPreferencesKey("use_dynamic_color")
        val KEY_APP_THEME = stringPreferencesKey("app_theme")
    }

    val openRouterApiKey: Flow<String> = context.dataStore.data.map { it[KEY_OPENROUTER_API_KEY] ?: "" }
    val groqApiKey: Flow<String> = context.dataStore.data.map { it[KEY_GROQ_API_KEY] ?: "" }
    val selectedProvider: Flow<ApiProvider> = context.dataStore.data.map {
        runCatching { ApiProvider.valueOf(it[KEY_SELECTED_PROVIDER] ?: ApiProvider.GROQ.name) }
            .getOrDefault(ApiProvider.GROQ)
    }
    val selectedModel: Flow<String> = context.dataStore.data.map {
        it[KEY_SELECTED_MODEL] ?: "llama-3.3-70b-versatile"
    }
    val compressionLevel: Flow<CompressionLevel> = context.dataStore.data.map {
        runCatching { CompressionLevel.valueOf(it[KEY_COMPRESSION_LEVEL] ?: CompressionLevel.MEDIUM.name) }
            .getOrDefault(CompressionLevel.MEDIUM)
    }
    val darkTheme: Flow<Boolean> = context.dataStore.data.map { it[KEY_DARK_THEME] ?: true }
    val amoledMode: Flow<Boolean> = context.dataStore.data.map { it[KEY_AMOLED_MODE] ?: false }
    val useDynamicColor: Flow<Boolean> = context.dataStore.data.map { it[KEY_DYNAMIC_COLOR] ?: false }
    val appTheme: Flow<AppTheme> = context.dataStore.data.map {
        runCatching { AppTheme.valueOf(it[KEY_APP_THEME] ?: AppTheme.CAVE.name) }
            .getOrDefault(AppTheme.CAVE)
    }

    suspend fun setOpenRouterApiKey(key: String) = context.dataStore.edit { it[KEY_OPENROUTER_API_KEY] = key }
    suspend fun setGroqApiKey(key: String) = context.dataStore.edit { it[KEY_GROQ_API_KEY] = key }
    suspend fun setSelectedProvider(provider: ApiProvider) = context.dataStore.edit { it[KEY_SELECTED_PROVIDER] = provider.name }
    suspend fun setSelectedModel(model: String) = context.dataStore.edit { it[KEY_SELECTED_MODEL] = model }
    suspend fun setCompressionLevel(level: CompressionLevel) = context.dataStore.edit { it[KEY_COMPRESSION_LEVEL] = level.name }
    suspend fun setDarkTheme(dark: Boolean) = context.dataStore.edit { it[KEY_DARK_THEME] = dark }
    suspend fun setAmoledMode(amoled: Boolean) = context.dataStore.edit { it[KEY_AMOLED_MODE] = amoled }
    suspend fun setUseDynamicColor(dynamic: Boolean) = context.dataStore.edit { it[KEY_DYNAMIC_COLOR] = dynamic }
    suspend fun setAppTheme(theme: AppTheme) = context.dataStore.edit { it[KEY_APP_THEME] = theme.name }
}
