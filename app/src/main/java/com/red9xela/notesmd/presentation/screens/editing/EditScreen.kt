package com.red9xela.notesmd.presentation.screens.editing

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditOff
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.halilibo.richtext.markdown.BasicMarkdown
import com.halilibo.richtext.ui.material3.RichText
import com.halilibo.richtext.commonmark.CommonmarkAstNodeParser

@Composable
fun EditScreen(
    modifier: Modifier = Modifier,
    noteId: Int,
    viewModel: EditScreenViewModel = hiltViewModel(),
    onFinished: () -> Unit
) {
    val state = viewModel.state.collectAsState()

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(state.value.isFinished) {
        if (state.value.isFinished) {
            onFinished()
        }
    }

    LaunchedEffect(Unit) {
        if (noteId == -1) {
            focusRequester.requestFocus()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(
                    onClick = {
                        viewModel.processCommand(EditNoteCommand.Back)
                    })
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ChangeModeButton(
                        onClick = {
                            viewModel.processCommand(EditNoteCommand.ChangeMode)
                        },
                        isEditMode = state.value.isEditMode
                    )
                    SaveNoteButton(
                        onClick = {
                            viewModel.processCommand(EditNoteCommand.Save)
                        },
                        state = state
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = modifier.padding(paddingValues)) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = state.value.title,
                onValueChange = {
                    viewModel.processCommand(EditNoteCommand.InputTitle(it))
                },
                colors = TextFieldDefaults.colors(
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp
                )
            )
            Content(
                modifier = Modifier
                    .weight(1f),
                isEditMode = state.value.isEditMode,
                markdownContent = state.value.markdownContent,
                onTextChanged = { newValue ->
                    viewModel.processCommand(
                        EditNoteCommand.InputContent(
                            newValue
                        )
                    )
                }
            )

        }
        SettingsBar(modifier = modifier.padding(paddingValues))
    }

}

@Composable
fun Content(
    modifier: Modifier,
    isEditMode: Boolean,
    markdownContent: String,
    onTextChanged: (String) -> Unit
) {
    val parser = CommonmarkAstNodeParser()
    val astNode = parser.parse(markdownContent)

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        item {
            when (isEditMode) {
                true -> TextField(
                    modifier = modifier,
                    value = markdownContent,
                    onValueChange = onTextChanged,
                    colors = TextFieldDefaults.colors(
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    )
                )

                false -> RichText(
                    modifier = modifier.padding(horizontal = 8.dp)
                ) {
                    BasicMarkdown(astNode = astNode)
                }
            }
        }
    }
}


@Composable
private fun SettingsBar(
    modifier: Modifier,
) {
    Row() {

    }
}

@Composable
private fun ChangeModeButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isEditMode: Boolean
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier
            .size(56.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = CircleShape
            ),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        if (isEditMode) {
            Icon(
                imageVector = Icons.Default.EditOff,
                contentDescription = ("Change Mode")
            )
        } else {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = ("Change Mode")
            )
        }
    }
}

@Composable
private fun SaveNoteButton(
    modifier: Modifier = Modifier,
    state: State<EditNoteState>,
    onClick: () -> Unit
) {
    val currentState = state.value.isSaveEnabled
    FilledIconButton(
        onClick = onClick,
        modifier = modifier
            .size(56.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = CircleShape
            ),
        enabled = currentState,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(
            imageVector = Icons.Default.Done,
            contentDescription = ("Save Note")
        )
    }
}

@Composable
private fun BackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier
            .size(56.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = CircleShape
            ),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = ("Back to all notes")
        )
    }
}