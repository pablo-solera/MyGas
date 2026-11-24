package org.pablo.mygas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.models.Municipality
import org.pablo.mygas.domain.models.Provincia
import org.pablo.mygas.domain.models.UserPreferences
import org.pablo.mygas.domain.repositories.UserPreferencesRepository
import org.pablo.mygas.domain.usecases.GetMunicipalitiesUseCase
import org.pablo.mygas.domain.usecases.GetStationsByMunicipalityUseCase
import org.pablo.mygas.ui.state.Loadable

data class HomeState(
    val provincia: Provincia = Provincia.MADRID,
    val municipio: String = "",
    val municipioId: Int? = null,
    val municipiosState: Loadable<List<Municipality>> = Loadable.Idle,
    val gasStationsState: Loadable<List<GasStation>> = Loadable.Idle,
) {
    val municipios: List<Municipality>?
        get() = (municipiosState as? Loadable.Content)?.value
    val gasStations: List<GasStation>?
        get() = (gasStationsState as? Loadable.Content)?.value
}

class HomeViewModel(
    private val getMunicipalities: GetMunicipalitiesUseCase,
    private val getStationsByMunicipality: GetStationsByMunicipalityUseCase,
    preferencesRepository: UserPreferencesRepository,
    private val cpuDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()
    val preferences: StateFlow<UserPreferences> = preferencesRepository.preferences

    private var municipiosJob: Job? = null
    private var gasStationsJob: Job? = null
    private var provinciaRequest = 0
    private var gasStationsRequest = 0

    init {
        selectProvincia(Provincia.MADRID)
    }

    fun selectProvincia(provincia: Provincia) {
        if (_uiState.value.provincia == provincia && _uiState.value.municipios != null) return

        municipiosJob?.cancel()
        gasStationsJob?.cancel()
        val request = ++provinciaRequest
        ++gasStationsRequest
        _uiState.update {
            it.copy(
                provincia = provincia,
                municipio = "",
                municipioId = null,
                municipiosState = Loadable.Loading,
                gasStationsState = Loadable.Idle,
            )
        }
        municipiosJob = viewModelScope.launch {
            when (val result = getMunicipalities(provincia.id)) {
                is Result.Success -> {
                    val municipios = withContext(cpuDispatcher) { result.data.sortedBy { it.name } }
                    ensureActive()
                    if (request != provinciaRequest) return@launch
                    _uiState.update { it.copy(municipiosState = Loadable.Content(municipios)) }
                }

                is Result.Error -> {
                    if (request != provinciaRequest) return@launch
                    _uiState.update { it.copy(municipiosState = Loadable.Failed(result.error)) }
                }
            }
        }
    }

    fun selectMunicipio(municipio: Municipality) = getGasolinerasByMunicipio(municipio.id)

    fun getGasolinerasByMunicipio(municipioId: Int) {
        if (_uiState.value.municipioId == municipioId && _uiState.value.gasStations != null) return

        gasStationsJob?.cancel()
        val request = ++gasStationsRequest
        _uiState.update { current ->
            current.copy(
                municipioId = municipioId,
                municipio = current.municipios?.find { it.id == municipioId }?.name.orEmpty(),
                gasStationsState = Loadable.Loading,
            )
        }
        gasStationsJob = viewModelScope.launch {
            when (val result = getStationsByMunicipality(municipioId)) {
                is Result.Success -> {
                    val stations = withContext(cpuDispatcher) {
                        result.data.sortedWith(compareBy(nullsLast()) { it.fuels.gasoline95.price })
                    }
                    ensureActive()
                    if (request != gasStationsRequest) return@launch
                    _uiState.update { it.copy(gasStationsState = Loadable.Content(stations)) }
                }

                is Result.Error -> {
                    if (request != gasStationsRequest) return@launch
                    _uiState.update { it.copy(gasStationsState = Loadable.Failed(result.error)) }
                }
            }
        }
    }

    fun retryMunicipios() {
        _uiState.update { it.copy(municipiosState = Loadable.Idle) }
        selectProvincia(_uiState.value.provincia)
    }

    fun retryGasolineras() {
        val id = _uiState.value.municipioId ?: return
        _uiState.update { it.copy(gasStationsState = Loadable.Idle) }
        getGasolinerasByMunicipio(id)
    }
}
