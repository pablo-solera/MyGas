package org.pablo.mygas.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class GasolineraFavoriteState(
    val isChecked: Boolean = false
)

class GasolineraFavoriteViewModel : ViewModel() {

    private val _state = MutableStateFlow(GasolineraFavoriteState())
    val state: StateFlow<GasolineraFavoriteState> = _state.asStateFlow()

    fun toggleChecked(checked: Boolean){
        _state.update { currentState ->
            currentState.copy(isChecked = checked)
        }
    }
}