package org.pablo.mygas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

data class GasolineraDetailState(
    val isChecked: Boolean = false,
)

class GasolineraFavoriteViewModel : ViewModel() {

    private var _state = MutableStateFlow(GasolineraDetailState())
    val state : StateFlow<GasolineraDetailState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GasolineraDetailState()
    )

    fun toggleChecked(isChecked: Boolean) {
        _state.value = state.value.copy(
            isChecked = isChecked
        )
    }
}