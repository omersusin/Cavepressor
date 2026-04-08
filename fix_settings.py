import re

file_path = 'app/src/main/java/com/cavepressor/ui/screens/SettingsScreen.kt'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

prefix = 'import androidx.compose.material.icons.filled.Memory\nimport androidx.compose.material.icons.filled.Cloud\nimport androidx.compose.material.icons.filled.PhoneAndroid\n'
if 'Icons.Default.Memory' not in content:
    content = content.replace('import androidx.compose.material.icons.filled.Palette', prefix + 'import androidx.compose.material.icons.filled.Palette')

engine_type_ui = '''
            // AI Engine Type
            item {
                SettingsSectionCard(title = "AI Engine Type", icon = Icons.Default.Memory) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EngineType.entries.filter { it != EngineType.NLP }.forEach { type ->
                            FilterChip(
                                selected = settings.engineType == type,
                                onClick = { viewModel.setEngineType(type) },
                                label = { Text(if (type == EngineType.CLOUD_LLM) "Cloud API" else "Local Phone") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (type == EngineType.CLOUD_LLM) Icons.Default.Cloud else Icons.Default.PhoneAndroid,
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
                            text = "Experimental: Runs an AI model locally using your phone's memory. No internet required. Download manager coming soon.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (settings.engineType == EngineType.CLOUD_LLM) {
'''

if '// AI Engine Type' not in content:
    content = content.replace('            // Provider seçimi', engine_type_ui + '            // Provider seçimi')
    content = content.replace('            // Temel Ayarlar', '            }\n\n            // Temel Ayarlar')
    
with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print('Done!')
