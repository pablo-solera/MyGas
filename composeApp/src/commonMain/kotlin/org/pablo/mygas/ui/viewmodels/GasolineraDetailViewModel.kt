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
import kotlinx.coroutines.launch
import org.pablo.mygas.data.GasolinerasRepository
import org.pablo.mygas.data.GasolinerasResponse
import org.pablo.mygas.data.ListaEESSPrecio
import org.pablo.mygas.data.MunicipioItem
import org.pablo.mygas.data.MunicipioService



class GasolineraDetailViewModel(
    private val gasolinerasRepository: GasolinerasRepository
): ViewModel() {
    private var _gasolinera = MutableStateFlow(GasolineraDetailState())


}

