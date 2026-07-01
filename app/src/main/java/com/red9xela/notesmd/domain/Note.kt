package com.red9xela.notesmd.domain

data class Note(
    val id: Int,
    val title: String,
    val markdownContent: String,
    val updatedAt: Long,
    val isPinned: Boolean,
)