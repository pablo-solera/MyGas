package org.pablo.mygas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.models.UserPreferences
import org.pablo.mygas.domain.repositories.UserPreferencesRepository
import org.pablo.mygas.domain.usecases.GetStationUseCase
import org.pablo.mygas.domain.usecases.ObserveFavoriteIdsUseCase
import org.pablo.mygas.ui.state.Loadable

data class GasolineraFavoriteState(
    val gasStationIds: List<Int> = emptyList(),
    val loadState: Loadable<List<GasStation>> = Loadable.Idle,
)

class GasolineraFavoriteViewModel(
    private val observeFavoriteIds: ObserveFavoriteIdsUseCase,
    private val getStation: GetStationUseCase,
    preferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(GasolineraFavoriteState())
    val state: StateFlow<GasolineraFavoriteState> = _state.asStateFlow()
    val preferences: StateFlow<UserPreferences> = preferencesRepository.preferences

    private var observationJob: Job? = null
    private val requestLimit = Semaphore(4)

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        if (observationJob?.isActive == true) return

        observationJob = viewModelScope.launch {
            observeFavoriteIds()
                .distinctUntilChanged()
                .collectLatest { ids ->
                    _state.update { current ->
                        val hasContent = current.loadState is Loadable.Content
                        current.copy(
                            gasStationIds = ids,
                            loadState = if (hasContent) current.loadState else Loadable.Loading,
                        )
                    }

                    if (ids.isEmpty()) {
                        _state.update { it.copy(loadState = Loadable.Content(emptyList())) }
                        return@collectLatest
                    }

                    val results = coroutineScope {
                        ids.map { id -> async { requestLimit.withPermit { getStation(id) } } }.awaitAll()
                    }
                    val stations = results.filterIsInstance<Result.Success<GasStation>>().map { it.data }
                    val firstError: NetworkError? =
                        results.filterIsInstance<Result.Error<NetworkError>>().firstOrNull()?.error

                    _state.update {
                        it.copy(
                            loadState = if (stations.isEmpty() && firstError != null) Loadable.Failed(firstError)
                            else Loadable.Content(stations),
                        )
                    }
                }
        }
    }

    fun retry() {
        observationJob?.cancel()
        observationJob = null
        _state.update { it.copy(loadState = Loadable.Idle) }
        observeFavorites()
    }
}
