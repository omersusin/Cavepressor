package com.omersusin.cavepressor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    onThemeSelected: (AppThemeType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Theme",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(AppThemeType.entries) { theme ->
                ThemeItem(
                    themeType = theme,
                    isSelected = theme == selectedTheme,
                    onClick = { onThemeSelected(theme) }
                )
            }
        }
    }
}

@Composable
private fun ThemeItem(
    themeType: AppThemeType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
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
            if (themeType == AppThemeType.CUSTOM) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Palette,
                        contentDescription = "Custom Theme",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                }
            } else {
                val colors = getQuadColors(themeType)
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

private fun getQuadColors(themeType: AppThemeType): List<Color> {
    return when (themeType) {
        AppThemeType.CRIMSON -> listOf(CrimsonPrimary, CrimsonSurfaceVariant, CrimsonTertiary, CrimsonBackground)
        AppThemeType.VIOLET -> listOf(VioletPrimary, VioletSurfaceVariant, VioletTertiary, VioletBackground)
        AppThemeType.OCEAN -> listOf(OceanPrimary, OceanSurfaceVariant, OceanTertiary, OceanBackground)
        AppThemeType.SAGE -> listOf(SagePrimary, SageSurfaceVariant, SageTertiary, SageBackground)
        AppThemeType.AMBER -> listOf(AmberPrimary, AmberSurfaceVariant, AmberTertiary, AmberBackground)
        AppThemeType.ROSE -> listOf(RosePrimary, RoseSurfaceVariant, RoseTertiary, RoseBackground)
        AppThemeType.MONO -> listOf(MonoPrimary, MonoSurfaceVariant, MonoTertiary, MonoBackground)
        AppThemeType.CUSTOM -> listOf(Color.Transparent, Color.Transparent, Color.Transparent, Color.Transparent)
    }
}
