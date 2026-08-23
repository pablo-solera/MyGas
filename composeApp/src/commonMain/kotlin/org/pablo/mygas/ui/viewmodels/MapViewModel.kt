package org.pablo.mygas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.pablo.mygas.core.utils.onError
import org.pablo.mygas.core.utils.onSuccess
import org.pablo.mygas.ui.state.Loadable
import org.pablo.mygas.data.toSafeDouble
import org.pablo.mygas.domain.models.FuelPrice
import org.pablo.mygas.domain.models.map.MapCoordinate
import org.pablo.mygas.domain.models.map.MapMarker
import org.pablo.mygas.domain.models.map.MapState
import org.pablo.mygas.domain.usecases.GetAllGasolinerasByMunicipioUseCase


class MapViewModel(
    private val getAllGasolinerasByMunicipioUseCase: GetAllGasolinerasByMunicipioUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapState())
    val uiState: StateFlow<MapState> = _uiState.asStateFlow()

    private var lastMunicipioId: Int? = null

    fun getGasolinerasByMunicipio(municipioId: Int) {
        lastMunicipioId = municipioId
        viewModelScope.launch {
            _uiState.update { it.copy(markersState = Loadable.Loading) }
            getAllGasolinerasByMunicipioUseCase(municipioId)
                .onSuccess { response ->
                    val markers = response.ListaEESSPrecio.mapNotNull {
                        val latitude = it.Latitud.toSafeDouble()
                        val longitude = it.longitud.toSafeDouble()
                        if (latitude == null || longitude == null) return@mapNotNull null

                        MapMarker(
                            id = it.IDEESS,
                            position = MapCoordinate(latitude, longitude),
                            title = it.rotulo,
                            brand = it.rotulo,
                            fuelPrice = FuelPrice(
                                it.precioGasolina95E5.toSafeDouble(),
                                it.precioGasolina95E5.toSafeDouble()
                            )
                        )
                    }
                    _uiState.update { currentState ->
                        currentState.copy(
                            markers = markers,
                            markersState = Loadable.Content(markers),
                        )
                    }

                }.onError { error ->
                    _uiState.update { it.copy(markersState = Loadable.Failed(error)) }
                }
        }
    }

    fun retry() {
        lastMunicipioId?.let(::getGasolinerasByMunicipio)
    }
}
