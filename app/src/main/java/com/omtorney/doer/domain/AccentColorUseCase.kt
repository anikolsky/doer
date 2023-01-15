package com.omtorney.doer.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class AccentColorUseCase @Inject constructor(repository: Repository) {

    private val scope = CoroutineScope(Dispatchers.Default)

    val execute = repository.getAccentColor.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = repository.getInitialColor
    )
}