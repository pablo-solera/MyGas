package org.pablo.mygas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.pablo.mygas.data.local.database.GasStationDao
import org.pablo.mygas.data.local.database.RoomGasStationAdapter
import org.pablo.mygas.data.remote.adapters.PrecioilGasStationAdapter
import org.pablo.mygas.data.toDomain
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.repositories.GasStationRepository
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.ui.state.Loadable

data class GasolineraFavoriteState(
    val gasStationIds: List<Int> = emptyList(),
    val gasStations: List<GasStation>? = emptyList(), // Lista con la información completa
    val isLoading: Boolean = false,
    val loadState: Loadable<List<GasStation>> = Loadable.Idle,
)

class GasolineraFavoriteViewModel(
    private val roomAdapter: RoomGasStationAdapter,
    private val precioilGasStationAdapter: PrecioilGasStationAdapter


) : ViewModel() {

    private val _state = MutableStateFlow(GasolineraFavoriteState())
    val state: StateFlow<GasolineraFavoriteState> = _state.asStateFlow()
    private var observationJob: Job? = null
    private val requestLimit = Semaphore(4)

    init {
        // Iniciamos la observación automáticamente al crear el ViewModel
        observeFavoriteIds()
    }

    fun observeFavoriteIds() {
        if (observationJob?.isActive == true) return

        observationJob = viewModelScope.launch {
            roomAdapter.observeFavoriteIds()
                .distinctUntilChanged()
                .collectLatest { ids ->
                _state.update { it.copy(gasStationIds = ids, isLoading = true, loadState = Loadable.Loading) }

                if (ids.isEmpty()) {
                    _state.update { it.copy(gasStations = emptyList(), isLoading = false, loadState = Loadable.Content(emptyList())) }
                    return@collectLatest
                }

                var failed = false
                val stations = coroutineScope {
                    ids.map { id ->
                        async {
                            requestLimit.withPermit {
                                try {
                                    precioilGasStationAdapter.getById(id).toDomain()
                                } catch (e: CancellationException) {
                                    throw e
                                } catch (_: Exception) {
                                    failed = true
                                    null
                                }
                            }
                        }
                    }.awaitAll().filterNotNull()
                }

                _state.update {
                    it.copy(
                        gasStations = stations,
                        isLoading = false,
                        loadState = if (failed) Loadable.Failed(NetworkError.UNKNOWN)
                        else Loadable.Content(stations),
                    )
                }
            }
        }
    }

    fun retry() {
        observationJob?.cancel()
        observationJob = null
        observeFavoriteIds()
    }

}
