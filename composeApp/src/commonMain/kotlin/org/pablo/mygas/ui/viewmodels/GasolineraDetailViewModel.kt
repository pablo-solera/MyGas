package org.pablo.mygas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.pablo.mygas.data.local.database.RoomGasStationAdapter
import org.pablo.mygas.data.remote.adapters.PrecioilGasStationAdapter
import org.pablo.mygas.data.toDomain
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.ui.state.Loadable


data class GasolineraDetailState(
    val isFavorite: Boolean? = false,
    val gasStation: GasStation? = null,
    val loadState: Loadable<GasStation> = Loadable.Idle,
)

class GasolineraDetailViewModel(
    private val precioilGasStationAdapter: PrecioilGasStationAdapter,
    private val roomAdapter: RoomGasStationAdapter
) : ViewModel() {

    private val _state = MutableStateFlow(GasolineraDetailState())
    val state: StateFlow<GasolineraDetailState> = _state.asStateFlow()


    fun getGasStationById(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(loadState = Loadable.Loading) }
            try {
                val gasStation = precioilGasStationAdapter.getById(id).toDomain()
                _state.update {
                    it.copy(gasStation = gasStation, loadState = Loadable.Content(gasStation))
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (_: Exception) {
                _state.update { it.copy(loadState = Loadable.Failed(NetworkError.UNKNOWN)) }
            }
        }
    }

    fun retry(id: Int) = getGasStationById(id)

    fun checkFavorite(id: Int) {
        viewModelScope.launch {
            roomAdapter.observeFavoriteIds()
                .map { ids -> id in ids }
                .distinctUntilChanged()
                .collect { isFavorite ->
                _state.update {
                    it.copy(
                        isFavorite = isFavorite
                    )
                }
            }
        }
    }

    fun insertGasStationId(id: Int) {
        viewModelScope.launch {
            roomAdapter.addFavorite(id)
        }
    }

    fun removeGasStationId(id: Int) {
        viewModelScope.launch {
            roomAdapter.removeFavorite(id)
        }
    }


}
