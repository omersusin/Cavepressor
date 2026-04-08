package com.omersusin.cavepressor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omersusin.cavepressor.ui.theme.*

@Composable
fun ThemeSelectorGrid(
    selectedTheme: AppThemeType,
    customColor: Color,
    onThemeSelected: (AppThemeType) -> Unit,
    onCustomThemeEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Theme",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val itemsPerRow = 4
            val items = AppThemeType.entries
            items.chunked(itemsPerRow).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (theme in rowItems) {
                        ThemeItem(
                            themeType = theme,
                            customColor = customColor,
                            isSelected = theme == selectedTheme,
                            onClick = { 
                                if (theme == AppThemeType.CUSTOM && selectedTheme == theme) {
                                    onCustomThemeEdit()
                                } else {
                                    if (theme == AppThemeType.CUSTOM) onCustomThemeEdit()
                                    onThemeSelected(theme)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    repeat(itemsPerRow - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeItem(
    themeType: AppThemeType,
    customColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        val quadShape = RoundedCornerShape(16.dp)
        val borderModifier = if (isSelected) {
            Modifier.border(3.dp, MaterialTheme.colorScheme.primary, quadShape)
        } else {
            Modifier
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(quadShape)
                .then(borderModifier)
                .clickable { onClick() }
        ) {
            val colors = getQuadColors(themeType, customColor)
            Column(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(colors[0]))
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(colors[1]))
                }
                Row(modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(colors[2]))
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(colors[3]))
                }
            }
            if (themeType == AppThemeType.CUSTOM) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Palette,
                        contentDescription = "Custom Theme",
                        tint = Color.White,
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color.Black.copy(alpha=0.4f), RoundedCornerShape(8.dp))
                            .padding(4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = themeType.name.lowercase().replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
            fontSize = 13.sp,
            maxLines = 1
        )
    }
}

private fun getQuadColors(themeType: AppThemeType, customColor: Color): List<Color> {
    return when (themeType) {
        AppThemeType.CRIMSON -> listOf(CrimsonPrimary, CrimsonSurfaceVariant, CrimsonTertiary, CrimsonBackground)
        AppThemeType.VIOLET -> listOf(VioletPrimary, VioletSurfaceVariant, VioletTertiary, VioletBackground)
        AppThemeType.OCEAN -> listOf(OceanPrimary, OceanSurfaceVariant, OceanTertiary, OceanBackground)
        AppThemeType.SAGE -> listOf(SagePrimary, SageSurfaceVariant, SageTertiary, SageBackground)
        AppThemeType.AMBER -> listOf(AmberPrimary, AmberSurfaceVariant, AmberTertiary, AmberBackground)
        AppThemeType.ROSE -> listOf(RosePrimary, RoseSurfaceVariant, RoseTertiary, RoseBackground)
        AppThemeType.MONO -> listOf(MonoPrimary, MonoSurfaceVariant, MonoTertiary, MonoBackground)
        AppThemeType.CUSTOM -> listOf(customColor, customColor.copy(alpha=0.7f), customColor.copy(alpha=0.5f), Color(0xFF333333))
    }
}

@Composable
fun CustomColorDialog(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    var red by remember { mutableStateOf(initialColor.red) }
    var green by remember { mutableStateOf(initialColor.green) }
    var blue by remember { mutableStateOf(initialColor.blue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pick Custom Color") },
        text = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(red, green, blue))
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Red", style = MaterialTheme.typography.labelMedium)
                Slider(value = red, onValueChange = { red = it }, colors = SliderDefaults.colors(thumbColor = Color.Red, activeTrackColor = Color.Red))
                
                Text("Green", style = MaterialTheme.typography.labelMedium)
                Slider(value = green, onValueChange = { green = it },  colors = SliderDefaults.colors(thumbColor = Color.Green, activeTrackColor = Color.Green))
                
                Text("Blue", style = MaterialTheme.typography.labelMedium)
                Slider(value = blue, onValueChange = { blue = it }, colors = SliderDefaults.colors(thumbColor = Color.Blue, activeTrackColor = Color.Blue))
            }
        },
        confirmButton = {
            TextButton(onClick = { onColorSelected(Color(red, green, blue)) }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
