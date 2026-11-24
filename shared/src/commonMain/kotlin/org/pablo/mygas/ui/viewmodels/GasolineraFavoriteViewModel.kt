package org.pablo.mygas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.NetworkException
import org.pablo.mygas.data.local.database.GasStationLocalDataSource
import org.pablo.mygas.data.remote.datasources.PrecioilGasStationDataSource
import org.pablo.mygas.data.toDomain
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.ui.state.Loadable

data class GasolineraFavoriteState(
    val gasStationIds: List<Int> = emptyList(),
    val loadState: Loadable<List<GasStation>> = Loadable.Idle,
)

class GasolineraFavoriteViewModel(
    private val favorites: GasStationLocalDataSource,
    private val stationSource: PrecioilGasStationDataSource,
) : ViewModel() {

    private val _state = MutableStateFlow(GasolineraFavoriteState())
    val state: StateFlow<GasolineraFavoriteState> = _state.asStateFlow()
    private var observationJob: Job? = null
    private val requestLimit = Semaphore(4)

    init {
        observeFavoriteIds()
    }

    fun observeFavoriteIds() {
        if (observationJob?.isActive == true) return

        observationJob = viewModelScope.launch {
            favorites.observeFavoriteIds()
                .distinctUntilChanged()
                .collectLatest { ids ->
                    _state.update { current ->
                        val previous = (current.loadState as? Loadable.Content)?.value
                        current.copy(
                            gasStationIds = ids,
                            loadState = if (previous != null) current.loadState else Loadable.Loading,
                        )
                    }

                    if (ids.isEmpty()) {
                        _state.update { it.copy(loadState = Loadable.Content(emptyList())) }
                        return@collectLatest
                    }

                    var firstError: NetworkError? = null
                    val stations = coroutineScope {
                        ids.map { id ->
                            async {
                                requestLimit.withPermit {
                                    try {
                                        stationSource.getById(id).toDomain()
                                    } catch (e: CancellationException) {
                                        throw e
                                    } catch (e: NetworkException) {
                                        firstError = firstError ?: e.error
                                        null
                                    } catch (_: Exception) {
                                        firstError = firstError ?: NetworkError.UNKNOWN
                                        null
                                    }
                                }
                            }
                        }.awaitAll().filterNotNull()
                    }

                    _state.update {
                        it.copy(
                            loadState = firstError
                                ?.takeIf { stations.isEmpty() }
                                ?.let { error -> Loadable.Failed(error) }
                                ?: Loadable.Content(stations),
                        )
                    }
                }
        }
    }

    fun retry() {
        observationJob?.cancel()
        observationJob = null
        _state.update { it.copy(loadState = Loadable.Idle) }
        observeFavoriteIds()
    }
}
