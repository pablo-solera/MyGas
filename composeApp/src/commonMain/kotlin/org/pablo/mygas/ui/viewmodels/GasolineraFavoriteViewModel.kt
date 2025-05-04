package org.pablo.mygas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.pablo.mygas.data.remote.responses.ComunidadAutonomaResponse
import org.pablo.mygas.data.remote.ListaEESSPrecio
import org.pablo.mygas.data.remote.MunicipioItem
import org.pablo.mygas.domain.GetAllComunidadesAutonomasUseCase
import org.pablo.mygas.domain.GetAllGasolinerasByMunicipioUseCase
import org.pablo.mygas.domain.GetAllMunicipiosByProvinciaUseCase
import org.pablo.mygas.utils.NetworkError
import org.pablo.mygas.utils.Provincia
import org.pablo.mygas.utils.onError
import org.pablo.mygas.utils.onSuccess

data class GasolineraFavoriteState(
    val isChecked: Boolean = false,
    val ccaas: List<ComunidadAutonomaResponse>? = null,
    val municipios: List<MunicipioItem>? = null,
    val isLoading: Boolean = false,
    val errorMessage: NetworkError? = null,
    val gasolineras: List<ListaEESSPrecio>? = null,
)

class GasolineraFavoriteViewModel(
    val getAllComunidadesAutonomasUseCase: GetAllComunidadesAutonomasUseCase,
    val getAllMunicipiosByProvinciaUseCase: GetAllMunicipiosByProvinciaUseCase,
    val getAllGasolinerasByMunicipioUseCase: GetAllGasolinerasByMunicipioUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GasolineraFavoriteState())
    val state: StateFlow<GasolineraFavoriteState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(isLoading = true)
            }
            getAllGasolinerasByMunicipioUseCase(Provincia.ALCALA_DE_HENARES.id)
                .onSuccess { response ->
                    _state.update { currentState ->
                        currentState.copy(
                            gasolineras = response.ListaEESSPrecio,
                            isLoading = false
                        )
                    }
                }.onError {
                    // Handle error
                    _state.update { currentState ->
                        currentState.copy(errorMessage = it)
                    }
                    println("Error garrafal: $it")
                }
        }
    }

    fun toggleChecked(checked: Boolean) {
        _state.update { currentState ->
            currentState.copy(isChecked = checked)
        }
    }

    fun getAllComunidadesAutonomas() {
        viewModelScope.launch {
            getAllComunidadesAutonomasUseCase()
                .onSuccess { comunidades ->
                    _state.update { currentState ->
                        currentState.copy(ccaas = comunidades)
                    }
                }.onError {
                    // Handle error
                    println("Error garrafal: $it")
                }
        }
    }
}