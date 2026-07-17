package com.red9xela.notesmd.presentation.screens.notes

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.red9xela.notesmd.R
import com.red9xela.notesmd.domain.Note
import com.red9xela.notesmd.presentation.utils.DateFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = hiltViewModel(),
    onNoteClick: (Note) -> Unit,
    onAddNoteClick: () -> Unit
) {

    val state by viewModel.state.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.processCommand(NotesCommand.SyncNotes)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBar(
                    modifier = Modifier
                        .weight(1f, fill = false),
                    query = state.query,
                    onQueryChange = {
                        viewModel.processCommand(NotesCommand.InputSearchQuery(it))
                    }
                )

                Spacer(modifier = Modifier.padding(horizontal = 8.dp))

                AddNoteButton(onClick = onAddNoteClick)
            }
        },
    ) { paddingValues ->
        val pinnedTitle = stringResource(R.string.pinned)
        val todayTitle = stringResource(R.string.today)
        val previous7DaysTitle = stringResource(R.string.previous_7_days)
        val previous30DaysTitle = stringResource(R.string.previous_30_days)
        val olderNotesTitle = stringResource(R.string.older_notes)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentPadding = paddingValues
        ) {

            item {
                Title(
                    modifier = modifier.padding(top = 24.dp),
                    text = stringResource(R.string.all_notes)
                )
            }

            if (state.folderUri == null) {
                item {
                    Column(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .padding(bottom = paddingValues.calculateBottomPadding()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        FolderPickerScreen(modifier = modifier) { uri ->
                            viewModel.processCommand(NotesCommand.SaveSelectedFolder(uri))
                        }
                    }
                }
            }
            notesSection(
                title = pinnedTitle,
                notes = state.pinnedNotes,
                onNoteClick = onNoteClick,
                onLongClick = { viewModel.processCommand(NotesCommand.SwitchPinnedStatus(it.id)) },
                onSwipe = { viewModel.processCommand(NotesCommand.DeleteNotes(it.id)) }
            )
            notesSection(
                title = todayTitle,
                notes = state.todayNotes,
                onNoteClick = onNoteClick,
                onLongClick = { viewModel.processCommand(NotesCommand.SwitchPinnedStatus(it.id)) },
                onSwipe = { viewModel.processCommand(NotesCommand.DeleteNotes(it.id)) })

            notesSection(
                title = previous7DaysTitle,
                notes = state.previousSevenDays,
                onNoteClick = onNoteClick,
                onLongClick = { viewModel.processCommand(NotesCommand.SwitchPinnedStatus(it.id)) },
                onSwipe = { viewModel.processCommand(NotesCommand.DeleteNotes(it.id)) }
            )
            notesSection(
                title = previous30DaysTitle,
                notes = state.previousThirtyDays,
                onNoteClick = onNoteClick,
                onLongClick = { viewModel.processCommand(NotesCommand.SwitchPinnedStatus(it.id)) },
                onSwipe = { viewModel.processCommand(NotesCommand.DeleteNotes(it.id)) }
            )
            notesSection(
                title = olderNotesTitle,
                notes = state.olderNotes,
                onNoteClick = onNoteClick,
                onLongClick = { viewModel.processCommand(NotesCommand.SwitchPinnedStatus(it.id)) },
                onSwipe = { viewModel.processCommand(NotesCommand.DeleteNotes(it.id)) }
            )
        }
    }
}

private fun LazyListScope.notesSection(
    title: String,
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    onLongClick: (Note) -> Unit,
    onSwipe: (Note) -> Unit
) {
    if (notes.isEmpty()) return

    item {
        Spacer(modifier = Modifier.height(16.dp))
    }
    item {
        Subtitle(text = title)
    }

    item {
        Spacer(modifier = Modifier.height(16.dp))
    }
    item {
        NotesGroupCard(
            notes = notes,
            onNoteClick = onNoteClick,
            onLongClick = onLongClick,
            onSwipe = onSwipe
        )
    }
}


@Composable
private fun SearchBar(
    modifier: Modifier,
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .height(56.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(16.dp)
            ),
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = stringResource(R.string.search),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search Notes",
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun AddNoteButton(
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
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = stringResource(R.string.add_note)
        )
    }
}

@Composable
private fun Title(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun Subtitle(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    onNoteClick: (Note) -> Unit,
    onLongClick: (Note) -> Unit,
    onSwipe: (Note) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onSwipe(note)
            }
            true
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val color = when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.8f)
                else -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .combinedClickable(
                    onClick = {
                        onNoteClick(note)
                    },
                    onLongClick = {
                        onLongClick(note)
                    }
                )
                .padding(16.dp)
        )
        {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = note.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis
                )

                if (note.isPinned) {
                    Icon(
                        imageVector = Icons.Filled.PushPin,
                        contentDescription = stringResource(R.string.pin_icon),
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    text = DateFormatter.formatDateToString(note.updatedAt),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.padding(horizontal = 8.dp))
                Text(
                    text = note.markdownContent,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Composable
fun NotesGroupCard(
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    onLongClick: (Note) -> Unit,
    onSwipe: (Note) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        notes.forEachIndexed { index, note ->
            key(note.id) {
                NoteCard(
                    note = note,
                    onNoteClick = onNoteClick,
                    onLongClick = onLongClick,
                    onSwipe = onSwipe
                )
                if (index < notes.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

@Composable
fun FolderPickerScreen(
    modifier: Modifier,
    onFolderSelected: (Uri) -> Unit
) {
    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree()) { uri ->
            if (uri != null) {
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, takeFlags)
                onFolderSelected(uri)
            }
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.where_should_you_keep_your_notes),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = stringResource(R.string.select_the_folder_on_your_device_where_your_markdown_files_are_or_will_be_located),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Button(onClick = { launcher.launch(null) }) {
            Text(stringResource(R.string.choose_folder))
        }
    }
}