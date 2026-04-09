package com.cavepressor.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.cavepressor.domain.model.CaveModel

@Composable
fun ModelSelector(
    models: List<CaveModel>,
    selectedModelId: String,
    onModelSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedModel = models.find { it.id == selectedModelId }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = selectedModel?.displayName ?: selectedModelId.substringAfterLast("/"),
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ArrowDropUp
                else Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 300.dp)
        ) {
            models.forEach { model ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = model.displayName,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onModelSelected(model.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
