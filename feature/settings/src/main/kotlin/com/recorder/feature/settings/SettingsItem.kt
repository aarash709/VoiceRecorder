package com.recorder.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.recorder.core.designsystem.theme.VoiceRecorderTheme

@Composable
internal fun SettingsItemWithSwitch(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String = "",
    isChecked: Boolean,
    onCheckChanged: (Boolean) -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .requiredHeight(80.dp)
            .clickable { onCheckChanged(!isChecked) },
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            Modifier
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, fontSize = 16.sp)
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.5f
                        )
                    )
                }
            }
            Switch(checked = isChecked, onCheckedChange = { onCheckChanged(!isChecked) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsItemWithOptions(
    modifier: Modifier = Modifier,
    title: String,
    currentActiveOption: String,
    options: @Composable () -> Unit,
) {
    var shouldShowOptions by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(currentActiveOption) {
        shouldShowOptions = false
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { shouldShowOptions = !shouldShowOptions },
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            Modifier
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 16.sp)
            CompositionLocalProvider(
                value = LocalContentColor provides MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.5f
                )
            ) {
                Row {
                    Text(text = currentActiveOption)
                    Icon(
                        imageVector = Icons.Default.UnfoldMore,
                        contentDescription = "more options"
                    )
                }
            }

        }
        if (shouldShowOptions) {
            BasicAlertDialog(
                onDismissRequest = { shouldShowOptions = !shouldShowOptions }
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp)
                ) {
                    options()
                }
            }
        }
    }
}

@Composable
internal fun SettingsItemWithAction(
    modifier: Modifier = Modifier,
    title: String,
    action: () -> Unit,
) {
    var isClicked by remember {
        mutableStateOf(false)
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                isClicked = !isClicked
                action()
            },
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            Modifier
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 16.sp)
            CompositionLocalProvider(
                value = LocalContentColor provides MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.5f
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = "more options"
                )
            }
        }
    }
}

@Composable
fun OptionsItem(
    modifier: Modifier = Modifier,
    optionName: String,
    description: String = "",
    isSelected: Boolean,
    isClickable: Boolean = true,
    onSelectOption: () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = isClickable) { onSelectOption() },
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        else MaterialTheme.colorScheme.surface
    ) {
        val color = if (isSelected) MaterialTheme.colorScheme.primary
        else LocalContentColor.current
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = optionName,
                    modifier = Modifier,
                    color = color
                )
                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        modifier = Modifier,
                        fontSize = 12.sp,
                        color = color
                    )
                }
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    tint = color,
                    contentDescription = "selected format icon"
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewOptionsItem(modifier: Modifier = Modifier) {
    VoiceRecorderTheme {
        OptionsItem(optionName = "option", isSelected = true, description = "") { }
    }
}
