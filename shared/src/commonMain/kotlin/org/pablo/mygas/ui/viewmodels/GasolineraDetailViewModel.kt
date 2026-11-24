package org.pablo.mygas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.NetworkException
import org.pablo.mygas.data.local.database.GasStationLocalDataSource
import org.pablo.mygas.data.remote.datasources.PrecioilGasStationDataSource
import org.pablo.mygas.data.toDomain
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.ui.state.Loadable

data class GasolineraDetailState(
    val isFavorite: Boolean = false,
    val loadState: Loadable<GasStation> = Loadable.Idle,
)

class GasolineraDetailViewModel(
    private val stationSource: PrecioilGasStationDataSource,
    private val favorites: GasStationLocalDataSource,
) : ViewModel() {

    private val _state = MutableStateFlow(GasolineraDetailState())
    val state: StateFlow<GasolineraDetailState> = _state.asStateFlow()

    private var loadJob: Job? = null
    private var favoriteJob: Job? = null

    fun getGasStationById(id: Int) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.update { it.copy(loadState = Loadable.Loading) }
            try {
                val gasStation = stationSource.getById(id).toDomain()
                _state.update { it.copy(loadState = Loadable.Content(gasStation)) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: NetworkException) {
                _state.update { it.copy(loadState = Loadable.Failed(e.error)) }
            } catch (_: Exception) {
                _state.update { it.copy(loadState = Loadable.Failed(NetworkError.UNKNOWN)) }
            }
        }
    }

    fun retry(id: Int) = getGasStationById(id)

    fun checkFavorite(id: Int) {
        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            favorites.observeFavoriteIds()
                .map { ids -> id in ids }
                .distinctUntilChanged()
                .collect { isFavorite -> _state.update { it.copy(isFavorite = isFavorite) } }
        }
    }

    fun insertGasStationId(id: Int) {
        viewModelScope.launch { favorites.addFavorite(id) }
    }

    fun removeGasStationId(id: Int) {
        viewModelScope.launch { favorites.removeFavorite(id) }
    }
}
