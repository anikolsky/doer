package com.omtorney.doer.domain.usecase

data class NoteUseCases(
    val getNotes: GetNotes,
    val getNote: GetNote,
    val addNote: AddNote,
    val deleteNote: DeleteNote,
    val pinNote: PinNote
)