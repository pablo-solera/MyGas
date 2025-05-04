package org.pablo.mygas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.pablo.mygas.data.remote.ListaEESSPrecio
import org.pablo.mygas.data.remote.MunicipioItem
import org.pablo.mygas.domain.GetAllGasolinerasByMunicipioUseCase
import org.pablo.mygas.domain.GetAllMunicipiosByProvinciaUseCase
import org.pablo.mygas.utils.Provincia
import org.pablo.mygas.utils.onError
import org.pablo.mygas.utils.onSuccess

data class HomeState(
    val municipio: String = "",
    val isLoading: Boolean = false,
    val municipios: List<MunicipioItem>? = null,
    val gasolineras: List<ListaEESSPrecio>? = null,
)

class HomeViewModel(
    val getAllGasolinerasByMunicipioUseCase: GetAllGasolinerasByMunicipioUseCase,
    val getAllMunicipiosByProvinciaUseCase: GetAllMunicipiosByProvinciaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(isLoading = true)
            }
            getAllMunicipiosByProvinciaUseCase(Provincia.MADRID.id)
                .onSuccess { response ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            municipios = response,
                            isLoading = false
                        )
                    }
                }.onError {
                    // Handle error
                    println("Error garrafal: $it")
                }
        }
    }

    fun getGasolinerasByMunicipio(municipioId: Int) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(isLoading = true)
            }
            getAllGasolinerasByMunicipioUseCase(municipioId)
                .onSuccess { response ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            gasolineras = response.ListaEESSPrecio.sortedBy {
                                it.precioGasolina95E5
                            },
                            municipio = currentState.municipios?.find { it.idMunicipio == municipioId.toString() }?.municipio
                                ?: "",
                            isLoading = false,
                        )
                    }

                }.onError {
                    // Handle error
                    println("Error garrafal: $it")
                }
        }
    }
}

