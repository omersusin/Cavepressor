import re

file_path = 'app/src/main/java/com/cavepressor/ui/screens/SettingsScreen.kt'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Replace if my previous script failed to find Temel Ayarlar.
if '}                // Görünüm' not in content and '            }\n\n            // Görünüm\n            item {' not in content:
    content = content.replace('            // Görünüm\n            item {', '            }\n\n            // Görünüm\n            item {')
    
with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print('Done!')
