package org.pablo.mygas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import org.pablo.mygas.core.state.SelectedMunicipalityStore
import org.pablo.mygas.ui.state.Loadable
import org.pablo.mygas.domain.models.FuelType
import org.pablo.mygas.domain.models.priceFor
import org.pablo.mygas.domain.models.map.MapCoordinate
import org.pablo.mygas.domain.models.map.MapMarker
import org.pablo.mygas.domain.models.map.MapState


class MapViewModel(
    private val selectionStore: SelectedMunicipalityStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapState())
    val uiState: StateFlow<MapState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            selectionStore.selection.collectLatest { selection ->
                if (selection == null) return@collectLatest
                val markers = selection.stations.mapNotNull { station ->
                    val coordinate = station.location
                    if (coordinate.latitude == 0.0 || coordinate.longitude == 0.0) return@mapNotNull null
                    MapMarker(
                        id = station.id.toString(),
                        position = MapCoordinate(coordinate.latitude, coordinate.longitude),
                        title = station.name,
                        brand = station.brand,
                        fuelPrice = station.fuels.priceFor(FuelType.GASOLINE_95),
                    )
                }
                _uiState.update { currentState ->
                    currentState.copy(
                        cameraPosition = currentState.cameraPosition.copy(
                            coordinates = markers.firstOrNull()?.position
                                ?: currentState.cameraPosition.coordinates,
                        ),
                        markers = markers,
                        markersState = Loadable.Content(markers),
                    )
                }
            }
        }
    }

    fun retry() {
        val current = _uiState.value
        _uiState.value = current.copy(markersState = Loadable.Content(current.markers))
    }
}
