package com.example.mvvmclean.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mvvmclean.domain.model.Note
import com.example.mvvmclean.presentation.state.UserAction
import com.example.mvvmclean.ui.theme.AndroidarchitecturetemplatesTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Create mode when [note] is null (editable fields, save button), otherwise
 * a read-only view of an existing note. After a save the ViewModel confirms
 * with a NavigateBack event.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    note: Note?,
    snackbarHostState: SnackbarHostState,
    onAction: (UserAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var title by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }
    val isCreateMode = note == null

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(if (isCreateMode) "New note" else "Note") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (note != null) {
                        IconButton(
                            onClick = {
                                onAction(UserAction.DeleteNote(note.id))
                                onBack()
                            },
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete note")
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (isCreateMode) {
                FloatingActionButton(
                    onClick = { onAction(UserAction.CreateNote(title, content)) },
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Save note")
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            if (isCreateMode) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    minLines = 6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                )
            } else if (note != null) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.headlineSmall,
                )
                if (note.updatedAt > 0L) {
                    Text(
                        text = "Last edited ${formatTimestamp(note.updatedAt)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }
        }
    }
}

private fun formatTimestamp(epochMillis: Long): String =
    DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm")
        .format(Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()))

@Preview(showBackground = true)
@Composable
private fun NoteDetailScreenPreview() {
    AndroidarchitecturetemplatesTheme {
        NoteDetailScreen(
            note = Note(1, "Groceries", "Milk, eggs, bread", 0, 1_700_000_000_000),
            snackbarHostState = SnackbarHostState(),
            onAction = {},
            onBack = {},
        )
    }
}
