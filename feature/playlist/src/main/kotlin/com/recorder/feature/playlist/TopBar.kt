package com.recorder.feature.playlist

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChecklistRtl
import androidx.compose.material.icons.outlined.Close
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
            AnimatedContent(
                targetState = isInEditMode,
                label = "Top bar Icon"
            ) { isInEditMode ->
                if (isInEditMode) {
                    IconButton(onClick = { onSelectedVoiceUpdate() }) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = "Clear selection Button"
                        )
                    }
                } else {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = "back icon"
                        )
                    }
                }
            }
        },
        actions = {
            AnimatedVisibility(visible = isInEditMode) {
                IconButton(onClick = { onIsAllSelected() }) {
                    Icon(
                        imageVector = Icons.Outlined.ChecklistRtl,
                        contentDescription = "Select all button"
                    )
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