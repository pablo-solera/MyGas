package org.pablo.mygas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.pablo.mygas.core.utils.Provincia
import org.pablo.mygas.core.utils.onError
import org.pablo.mygas.core.utils.onSuccess
import org.pablo.mygas.data.remote.MunicipioItem
import org.pablo.mygas.data.toDomain
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.usecases.GetAllGasolinerasByMunicipioUseCase
import org.pablo.mygas.domain.usecases.GetAllMunicipiosByProvinciaUseCase
import org.pablo.mygas.domain.usecases.GetGasolineraByIdUseCase

data class HomeState(
    val municipio: String = "",
    val isLoading: Boolean = false,
    val municipios: List<MunicipioItem>? = null,
    val gasStations: List<GasStation>? = null,
)

class HomeViewModel(
    val getAllGasolinerasByMunicipioUseCase: GetAllGasolinerasByMunicipioUseCase,
    val getAllMunicipiosByProvinciaUseCase: GetAllMunicipiosByProvinciaUseCase,
    val getGasolineraByIdUseCase: GetGasolineraByIdUseCase
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
                            gasStations = response.ListaEESSPrecio.map { it.toDomain() }.sortedBy {
                                it.fuels.gasoline95.price
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

