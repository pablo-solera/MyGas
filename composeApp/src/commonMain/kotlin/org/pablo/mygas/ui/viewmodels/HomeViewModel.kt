package org.pablo.mygas.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.Headers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.pablo.mygas.data.GasolinerasRepository
import org.pablo.mygas.data.GasolinerasResponse
import org.pablo.mygas.data.ListaEESSPrecio
import org.pablo.mygas.data.MunicipioItem
import org.pablo.mygas.data.MunicipioService

data class HomeState(
    val municipio: String = "",
    val isLoading: Boolean = false,
    val municipios: List<MunicipioItem> = emptyList(),
    val gasolineras: List<ListaEESSPrecio> = emptyList(),
    )

class HomeViewModel(
    private val gasolinerasRepository: GasolinerasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    private val _municipio = MutableStateFlow("")
    val municipio: String get() = _municipio.value

    private val _municipios = MutableStateFlow<List<MunicipioItem>>(emptyList())
    val municipios: List<MunicipioItem> get() = _municipios.value

    private val _gasolineras = MutableStateFlow<List<ListaEESSPrecio>>(emptyList())
    val gasolineras: List<ListaEESSPrecio> get() = _gasolineras.value

    var state by mutableStateOf(UiState())
        private set

    data class UiState(
        val isLoading: Boolean = false,
        //val municipios: List<MunicipioItem> = emptyList(),
        //var gasolineras: List<ListaEESSPrecio> = emptyList(),
        //val gasolinerasOrdenadas: List<ListaEESSPrecio> = emptyList()
    )

    init {
        viewModelScope.launch {
            state = UiState(isLoading = true)
            _municipios.value = gasolinerasRepository.getMunicipiosByProvincia("28")
            state = UiState(
                isLoading = false,
                /*gasolinerasAlcala = municipioService.getEstacionesTerrestresByMunicipio("4280")
                    .body(),*/

                //municipios = gasolinerasRepository.getMunicipiosByProvincia("28"),
                //gasolineras = gasolinerasRepository.getGasolinerasByMunicipio("4280"),
                //gasolinerasOrdenadas = gasolinerasRepository.getGasolinerasByMunicipio("4358")
                //.sortedBy { it.precioGasolina95E5 }
            )

        }
    }

    fun getGasolinerasByMunicipio(municipioId: String) {
        viewModelScope.launch {
            state = UiState(isLoading = true)
            _gasolineras.value = gasolinerasRepository.getGasolinerasByMunicipio(municipioId)
                .sortedBy { it.precioGasolina95E5 }
            _municipio.value = municipios.find { it.idMunicipio == municipioId }?.municipio ?: ""
            state = UiState(isLoading = false)

            //state.gasolineras  = municipioService.getEstacionesTerrestresByMunicipio(municipioId)
            //   .body<GasolinerasResponse>().ListaEESSPrecio
            /*state = UiState(isLoading = true)
            state = UiState(
                 gasolineras = municipioService.getEstacionesTerrestresByMunicipio(municipioId)
                     .body<GasolinerasResponse>().ListaEESSPrecio,
                isLoading = false,
            )*/
        }
    }
}

