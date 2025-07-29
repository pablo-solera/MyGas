package org.pablo.mygas.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import org.pablo.mygas.data.remote.GasolinerasRepository



data class GasolineraDetailState(
    val isFavorite: Boolean? = false,
)

class GasolineraDetailViewModel(
    private val gasolinerasRepository: GasolinerasRepository
): ViewModel() {

    private val _state = MutableStateFlow(GasolineraDetailState())
    val state: StateFlow<GasolineraDetailState> = _state.asStateFlow()


    fun openMap(){

    }

}

