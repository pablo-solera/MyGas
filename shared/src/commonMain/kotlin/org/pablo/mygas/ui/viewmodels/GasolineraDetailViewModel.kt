package org.pablo.mygas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.models.UserPreferences
import org.pablo.mygas.domain.repositories.UserPreferencesRepository
import org.pablo.mygas.domain.usecases.GetStationUseCase
import org.pablo.mygas.domain.usecases.ObserveFavoriteIdsUseCase
import org.pablo.mygas.domain.usecases.SetFavoriteUseCase
import org.pablo.mygas.ui.state.Loadable

data class GasolineraDetailState(
    val isFavorite: Boolean = false,
    val loadState: Loadable<GasStation> = Loadable.Idle,
)

class GasolineraDetailViewModel(
    private val getStation: GetStationUseCase,
    private val observeFavoriteIds: ObserveFavoriteIdsUseCase,
    private val setFavorite: SetFavoriteUseCase,
    preferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(GasolineraDetailState())
    val state: StateFlow<GasolineraDetailState> = _state.asStateFlow()
    val preferences: StateFlow<UserPreferences> = preferencesRepository.preferences

    private var loadJob: Job? = null
    private var favoriteJob: Job? = null

    fun getGasStationById(id: Int) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.update { it.copy(loadState = Loadable.Loading) }
            val loadState = when (val result = getStation(id)) {
                is Result.Success -> Loadable.Content(result.data)
                is Result.Error -> Loadable.Failed(result.error)
            }
            _state.update { it.copy(loadState = loadState) }
        }
    }

    fun retry(id: Int) = getGasStationById(id)

    fun checkFavorite(id: Int) {
        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            observeFavoriteIds()
                .map { ids -> id in ids }
                .distinctUntilChanged()
                .collect { isFavorite -> _state.update { it.copy(isFavorite = isFavorite) } }
        }
    }

    fun insertGasStationId(id: Int) {
        viewModelScope.launch { setFavorite(id, true) }
    }

    fun removeGasStationId(id: Int) {
        viewModelScope.launch { setFavorite(id, false) }
    }
}
