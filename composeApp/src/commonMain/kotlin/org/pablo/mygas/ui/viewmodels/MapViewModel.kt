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
import org.pablo.mygas.data.toSafeDouble
import org.pablo.mygas.domain.models.FuelPrice
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.models.map.MapCoordinate
import org.pablo.mygas.domain.models.map.MapMarker
import org.pablo.mygas.domain.models.map.MapState
import org.pablo.mygas.domain.usecases.GetAllGasolinerasByMunicipioUseCase
import org.pablo.mygas.domain.usecases.GetAllMunicipiosByProvinciaUseCase
import org.pablo.mygas.domain.usecases.GetGasolineraByIdUseCase


class MapViewModel(
    private val getAllGasolinerasByMunicipioUseCase: GetAllGasolinerasByMunicipioUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapState())
    val uiState: StateFlow<MapState> = _uiState.asStateFlow()

    init {
        getGasolinerasByMunicipio(4280)
    }

    fun getGasolinerasByMunicipio(municipioId: Int) {
        viewModelScope.launch {
//            _uiState.update { currentState ->
//                currentState.copy(isLoading = true)
//            }
            getAllGasolinerasByMunicipioUseCase(municipioId)
                .onSuccess { response ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            markers = response.ListaEESSPrecio.map {

                                MapMarker(
                                    id = it.IDEESS,
                                    position = MapCoordinate(
                                        it.Latitud.toSafeDouble()!!,
                                        it.longitud.toSafeDouble()!!
                                    ),
                                    title = it.rotulo,
                                    brand = it.rotulo,
                                    fuelPrice = FuelPrice(
                                        it.precioGasolina95E5.toSafeDouble(),
                                        it.precioGasolina95E5.toSafeDouble()
                                    )
                                )
                            }
                        )
                    }

                }.onError {
                    // Handle error
                    println("Error garrafal: $it")
                }
        }
    }
}

