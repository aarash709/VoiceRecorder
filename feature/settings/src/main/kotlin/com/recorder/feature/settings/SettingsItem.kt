package com.recorder.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

@Composable
internal fun SettingsItemWithSwitch(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String = "",
) {
    var isClicked by remember {
        mutableStateOf(false)
    }
    Surface(modifier = Modifier
        .fillMaxWidth()
        .requiredHeight(80.dp)
        .clickable { isClicked = !isClicked }
            then modifier) {
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
            Switch(checked = isClicked, onCheckedChange = { isClicked = it })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsItemWithOptions(
    modifier: Modifier = Modifier,
    title: String,
    currentOption: String,
    options: @Composable ColumnScope.() -> Unit,
) {
    var shouldShowOptions by remember {
        mutableStateOf(false)
    }
    Surface(modifier = Modifier
        .fillMaxWidth()
        .clickable { shouldShowOptions = !shouldShowOptions }
            then modifier) {
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
                    Text(text = currentOption)
                    Icon(
                        imageVector = Icons.Default.UnfoldMore,
                        contentDescription = "more options"
                    )
                }
            }

        }
        if (shouldShowOptions) {
            BasicAlertDialog(
                onDismissRequest = { shouldShowOptions = !shouldShowOptions },
                modifier = Modifier
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
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
    Surface(modifier = Modifier
        .fillMaxWidth()
        .clickable { isClicked = !isClicked }
            then modifier) {
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

