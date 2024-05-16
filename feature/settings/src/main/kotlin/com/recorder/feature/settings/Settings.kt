package com.recorder.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun Settings() {
    val settingsViewModel = hiltViewModel<SettingsViewModel>()
    SettingsContent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(modifier: Modifier = Modifier) {

    Column() {
        MediumTopAppBar(
            title = { Text("Settings") },
//            modifier =,
            navigationIcon = {},
            actions = {},
//            windowInsets =,
//            colors =,
            scrollBehavior = null
        )
        //settings sections
    }
}

@PreviewLightDark
@Composable
private fun SettingsPreview() {
    SettingsContent()
}