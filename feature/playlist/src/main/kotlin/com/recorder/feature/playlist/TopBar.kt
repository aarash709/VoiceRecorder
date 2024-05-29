package com.recorder.feature.playlist

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChecklistRtl
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistTopBar(
    isInEditMode: Boolean,
    selectedVoices: Set<String>,
    scrollBehavior: TopAppBarScrollBehavior,
    onSelectedVoiceUpdate: () -> Unit,
    onIsAllSelected: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onBackPressed: () -> Unit,
) {
    MediumTopAppBar(
        title = {
            AnimatedContent(
                targetState = isInEditMode,
                label = "Title Animation"
            ) { inEditMode ->
                if (inEditMode)
                    Text(text = "${selectedVoices.count()} item selected")
                else {
                    Text(
                        text = "Recordings",
                    )
                }
            }
        },
        navigationIcon = {
            AnimatedVisibility(
                visible = isInEditMode,
                enter = fadeIn(tween(150)),
                exit = fadeOut(tween(150)),
                label = "Top bar Icon"
            ) {
                IconButton(onClick = { onSelectedVoiceUpdate() }) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "Clear selection Button"
                    )
                }
            }
        },
        actions = {
            AnimatedContent(targetState = isInEditMode) {
                if (it) {
                    IconButton(onClick = { onIsAllSelected() }) {
                        Icon(
                            imageVector = Icons.Outlined.ChecklistRtl,
                            contentDescription = "Select all button"
                        )
                    }
                } else {
                    IconButton(onClick = { onNavigateToSettings() }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Select all button"
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults
            .mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
        scrollBehavior = scrollBehavior
    )
}