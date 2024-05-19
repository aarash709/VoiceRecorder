package com.recorder.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.recorder.core.designsystem.theme.VoiceRecorderTheme

@Composable
fun Settings() {
    val settingsViewModel = hiltViewModel<SettingsViewModel>()
    SettingsContent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(modifier: Modifier = Modifier) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Surface(
        modifier = Modifier
            .fillMaxSize()
                then modifier
    ) {
        Column() {
            MediumTopAppBar(
                title = { Text("Settings") },
//            modifier =,
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "back button"
                    )
                },
                actions = {},
//            windowInsets =,
//            colors =,
                scrollBehavior = scrollBehavior
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                SettingsItemWithSwitch(
                    title = "Earpiece mode",
                    subtitle = "Play audio using the earpiece speaker"
                )
                SettingsItemWithSwitch(
                    title = "Name recordings manually"
                )
                SettingsItemWithOptions(
                    title = "Recording format",
                    currentOption = "m4a",
                    options = {
                        repeat(3) {
                            Surface(modifier = modifier.fillMaxWidth(.5f)) {
                                Text(text = "$it:text")
                            }
                        }
                    }
                )
                SettingsItemWithOptions(
                    title = "Recording quality",
                    currentOption = "Standard",
                    options = {
                        Surface(modifier = modifier) {
                            Text(text = "Low")
                        }
                        Surface(modifier = modifier) {
                            Text(text = "Standard")
                        }
                        Surface(modifier = modifier) {
                            Text(text = "High")

                        }
                    }
                )
                SettingsItemWithAction(
                    title = "Clear data",
                    action = {}
                )
                SettingsItemWithAction(
                    title = "Recently deleted items",
                    action = {}
                )
            }
        }
    }
}


@PreviewLightDark
@Composable
private fun SettingsPreview() {
    VoiceRecorderTheme {
        SettingsContent()
    }
}