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
import org.pablo.mygas.core.utils.Provincia
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.data.remote.MunicipioItem
import org.pablo.mygas.data.toDomain
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.models.withAveragePrices
import org.pablo.mygas.domain.usecases.GetAllGasolinerasByMunicipioUseCase
import org.pablo.mygas.domain.usecases.GetAllMunicipiosByProvinciaUseCase
import org.pablo.mygas.ui.state.Loadable

data class HomeState(
    val provincia: Provincia = Provincia.MADRID,
    val municipio: String = "",
    val municipioId: Int? = null,
    val isLoading: Boolean = false,
    val isMunicipiosLoading: Boolean = false,
    val isGasStationsLoading: Boolean = false,
    val municipios: List<MunicipioItem>? = null,
    val gasStations: List<GasStation>? = null,
    val municipiosState: Loadable<List<MunicipioItem>> = Loadable.Idle,
    val gasStationsState: Loadable<List<GasStation>> = Loadable.Idle,
)

class HomeViewModel(
    private val getAllGasolinerasByMunicipioUseCase: GetAllGasolinerasByMunicipioUseCase,
    private val getAllMunicipiosByProvinciaUseCase: GetAllMunicipiosByProvinciaUseCase,
    private val cpuDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()
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
        _uiState.update { currentState ->
            currentState.copy(
                provincia = provincia,
                municipio = "",
                municipioId = null,
                municipios = null,
                gasStations = null,
                municipiosState = Loadable.Loading,
                gasStationsState = Loadable.Idle,
                isLoading = true,
                isMunicipiosLoading = true,
                isGasStationsLoading = false
            )
        }
        municipiosJob = viewModelScope.launch {
            when (val result = getAllMunicipiosByProvinciaUseCase(provincia.id)) {
                is Result.Success -> {
                    val municipios = withContext(cpuDispatcher) {
                        result.data.sortedBy { it.municipio }
                    }
                    ensureActive()
                    if (request != provinciaRequest) return@launch
                    _uiState.update { currentState ->
                        currentState.copy(
                            municipios = municipios,
                            municipiosState = Loadable.Content(municipios),
                            isLoading = currentState.isGasStationsLoading,
                            isMunicipiosLoading = false
                        )
                    }
                }

                is Result.Error -> {
                    if (request != provinciaRequest) return@launch
                    _uiState.update { currentState ->
                        currentState.copy(
                            municipiosState = Loadable.Failed(result.error),
                            isLoading = currentState.isGasStationsLoading,
                            isMunicipiosLoading = false
                        )
                    }
                }
            }
        }
    }

    fun getGasolinerasByMunicipio(municipioId: Int) {
        if (_uiState.value.municipioId == municipioId && _uiState.value.gasStations != null) return

        gasStationsJob?.cancel()
        val request = ++gasStationsRequest
        _uiState.update { currentState ->
            currentState.copy(
                municipioId = municipioId,
                municipio = currentState.municipios?.find {
                    it.idMunicipio == municipioId.toString()
                }?.municipio ?: "",
                gasStations = null,
                isLoading = true,
                gasStationsState = Loadable.Loading,
                isMunicipiosLoading = false,
                isGasStationsLoading = true
            )
        }
        gasStationsJob = viewModelScope.launch {
            when (val result = getAllGasolinerasByMunicipioUseCase(municipioId)) {
                is Result.Success -> {
                    val stations = withContext(cpuDispatcher) {
                        result.data.stations.toDomain()
                            .withAveragePrices()
                            .sortedWith(compareBy(nullsLast()) { it.fuels.gasoline95.price })
                    }
                    ensureActive()
                    if (request != gasStationsRequest) return@launch
                    _uiState.update { currentState ->
                        currentState.copy(
                            gasStations = stations,
                            gasStationsState = Loadable.Content(stations),
                            isLoading = currentState.isMunicipiosLoading,
                            isGasStationsLoading = false,
                        )
                    }
                }

                is Result.Error -> {
                    if (request != gasStationsRequest) return@launch
                    _uiState.update { currentState ->
                        currentState.copy(
                            gasStationsState = Loadable.Failed(result.error),
                            isLoading = currentState.isMunicipiosLoading,
                            isGasStationsLoading = false
                        )
                    }
                }
            }
        }
    }

    fun retryMunicipios() {
        _uiState.update { it.copy(municipios = null) }
        selectProvincia(_uiState.value.provincia)
    }

    fun retryGasolineras() {
        val id = _uiState.value.municipioId ?: return
        _uiState.update { it.copy(gasStations = null) }
        getGasolinerasByMunicipio(id)
    }
}
