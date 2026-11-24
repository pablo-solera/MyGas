package org.pablo.mygas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.domain.models.FuelType
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.models.UserPreferences
import org.pablo.mygas.domain.models.map.MapCameraPosition
import org.pablo.mygas.domain.models.map.MapCoordinate
import org.pablo.mygas.domain.models.map.MapMarker
import org.pablo.mygas.domain.models.map.MapState
import org.pablo.mygas.domain.models.map.MapViewport
import org.pablo.mygas.domain.models.priceFor
import org.pablo.mygas.domain.repositories.UserPreferencesRepository
import org.pablo.mygas.domain.usecases.GetStationsInAreaUseCase

data class MapScreenState(
    val map: MapState = MapState(),
    val isSearching: Boolean = false,
    val canSearchHere: Boolean = false,
    val zoomInRequired: Boolean = false,
    val error: NetworkError? = null,
    val stationCount: Int = 0,
    val hasSearched: Boolean = false,
    val selectedStation: GasStation? = null,
    val preferences: UserPreferences = UserPreferences(),
)

class MapViewModel(
    private val getStationsInArea: GetStationsInAreaUseCase,
    private val preferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(
        MapScreenState(preferences = preferencesRepository.preferences.value)
    )
    val state: StateFlow<MapScreenState> = _state.asStateFlow()

    private var stations: List<GasStation> = emptyList()
    private var viewport: MapViewport? = null
    private var lastSearchedViewport: MapViewport? = null
    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            preferencesRepository.preferences.collect { preferences ->
                _state.update { current ->
                    current.copy(
                        preferences = preferences,
                        map = current.map.copy(
                            markers = buildMarkers(
                                stations,
                                preferences.favoriteFuel,
                                current.selectedStation?.id,
                                viewport,
                            )
                        )
                    )
                }
            }
        }
    }

    fun onViewportChanged(newViewport: MapViewport) {
        viewport = newViewport
        val lastSearched = lastSearchedViewport
        if (lastSearched == null) {
            search(newViewport)
            return
        }
        _state.update {
            it.copy(
                map = it.map.copy(
                    cameraPosition = MapCameraPosition(newViewport.center, newViewport.zoom)
                ),
                canSearchHere = newViewport.differsSignificantlyFrom(lastSearched) && !it.isSearching,
                zoomInRequired = it.zoomInRequired && newViewport.zoom < MIN_SEARCH_ZOOM,
            )
        }
    }

    fun searchHere() {
        viewport?.let(::search)
    }

    fun onMarkerClick(markerId: String) {
        val station = stations.firstOrNull { it.id.toString() == markerId } ?: return
        select(station)
    }

    fun dismissSelection() = select(null)

    fun consumeError() = _state.update { it.copy(error = null) }

    private fun select(station: GasStation?) {
        _state.update { current ->
            current.copy(
                selectedStation = station,
                map = current.map.copy(
                    markers = buildMarkers(
                        stations,
                        current.preferences.favoriteFuel,
                        station?.id,
                        viewport,
                    )
                ),
            )
        }
    }

    private fun search(target: MapViewport) {
        if (target.zoom < MIN_SEARCH_ZOOM) {
            _state.update {
                it.copy(
                    map = it.map.copy(cameraPosition = MapCameraPosition(target.center, target.zoom)),
                    zoomInRequired = true,
                    canSearchHere = false,
                )
            }
            return
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _state.update { it.copy(isSearching = true, error = null, zoomInRequired = false) }
            when (val result = getStationsInArea(target.bounds)) {
                is Result.Success -> {
                    stations = result.data
                    lastSearchedViewport = target
                    _state.update { current ->
                        val keepSelected = current.selectedStation
                            ?.let { selected -> stations.firstOrNull { it.id == selected.id } }
                        current.copy(
                            isSearching = false,
                            canSearchHere = false,
                            stationCount = stations.size,
                            hasSearched = true,
                            selectedStation = keepSelected,
                            map = current.map.copy(
                                cameraPosition = MapCameraPosition(target.center, target.zoom),
                                markers = buildMarkers(
                                    stations,
                                    current.preferences.favoriteFuel,
                                    keepSelected?.id,
                                    target,
                                ),
                            ),
                        )
                    }
                }

                is Result.Error -> _state.update {
                    it.copy(isSearching = false, error = result.error, canSearchHere = true)
                }
            }
        }
    }

    companion object {
        const val MIN_SEARCH_ZOOM = 11f
        const val MAX_MARKERS = 400

        internal fun buildMarkers(
            stations: List<GasStation>,
            fuel: FuelType,
            selectedId: Int?,
            around: MapViewport?,
        ): List<MapMarker> {
            val ordered = if (around == null || stations.size <= MAX_MARKERS) {
                stations
            } else {
                val centerLat = around.center.latitude
                val centerLon = around.center.longitude
                stations.sortedBy { station ->
                    val deltaLat = station.location.latitude - centerLat
                    val deltaLon = station.location.longitude - centerLon
                    deltaLat * deltaLat + deltaLon * deltaLon
                }.take(MAX_MARKERS)
            }
            return ordered.map { station ->
                MapMarker(
                    id = station.id.toString(),
                    position = MapCoordinate(station.location.latitude, station.location.longitude),
                    title = station.name,
                    brand = station.brand,
                    isSelected = station.id == selectedId,
                    fuelPrice = station.fuels.priceFor(fuel),
                )
            }
        }
    }
}
