package org.pablo.mygas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.pablo.mygas.domain.models.ThemeMode
import org.pablo.mygas.domain.repositories.UserPreferencesRepository

class ThemeViewModel(preferencesRepository: UserPreferencesRepository) : ViewModel() {
    val themeMode: StateFlow<ThemeMode> = preferencesRepository.preferences
        .map { it.themeMode }
        .stateIn(viewModelScope, SharingStarted.Eagerly, preferencesRepository.preferences.value.themeMode)
}
