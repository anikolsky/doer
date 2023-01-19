package com.omtorney.doer.notes.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omtorney.doer.core.domain.usecase.NoteUseCases
import com.omtorney.doer.settings.domain.usecase.SettingsUseCases
import com.omtorney.doer.core.model.Note
import com.omtorney.doer.core.util.Constants
import com.omtorney.doer.notes.util.NoteOrder
import com.omtorney.doer.notes.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val settingsUseCases: SettingsUseCases
) : ViewModel() {

    private val _state = mutableStateOf(NotesState())
    val state: State<NotesState> = _state

    private var deletedNote: Note? = null

    private var getNotesJob: Job? = null

    val accentColor = settingsUseCases.getAccentColor.invoke().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Constants.initialColor
    )

    val secondaryColor = settingsUseCases.getSecondaryColor.invoke().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Constants.initialColor
    )

    val lineDividerState = settingsUseCases.getLineDivideState.invoke().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = true
    )

    init {
        getNotes(NoteOrder.DateCreated(OrderType.Descending))
    }

    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.Order -> {
                if (state.value.noteOrder::class == event.noteOrder::class &&
                    state.value.noteOrder.orderType == event.noteOrder.orderType
                ) {
                    return
                }
                getNotes(event.noteOrder)
            }
            is NotesEvent.Delete -> {
                viewModelScope.launch {
                    noteUseCases.deleteNote(event.note)
                    deletedNote = event.note
                }
            }
            is NotesEvent.Pin -> {
                viewModelScope.launch {
                    val notePinned = event.note.copy(isPinned = !event.note.isPinned)
                    noteUseCases.addNote(notePinned)
                }
            }
            is NotesEvent.RestoreNote -> {
                viewModelScope.launch {
                    noteUseCases.addNote(deletedNote ?: return@launch)
                    deletedNote = null
                }
            }
            is NotesEvent.ToggleOrderSection -> {
                _state.value = state.value.copy(
                    isOrderSectionVisible = !state.value.isOrderSectionVisible
                )
            }
        }
    }

    private fun getNotes(noteOrder: NoteOrder) {
        getNotesJob?.cancel()
        getNotesJob = noteUseCases.getNotes(noteOrder)
            .onEach { notes ->
                _state.value = state.value.copy(
                    notes = notes,
                    noteOrder = noteOrder
                )
            }
            .launchIn(viewModelScope)
    }
}