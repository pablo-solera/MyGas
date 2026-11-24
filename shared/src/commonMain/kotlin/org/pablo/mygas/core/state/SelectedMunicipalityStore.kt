package org.pablo.mygas.core.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.pablo.mygas.domain.models.GasStation

data class SelectedMunicipality(
    val id: Int,
    val stations: List<GasStation> = emptyList(),
)

class SelectedMunicipalityStore {
    private val _selection = MutableStateFlow<SelectedMunicipality?>(null)
    val selection: StateFlow<SelectedMunicipality?> = _selection.asStateFlow()

    fun clear() {
        _selection.value = null
    }

    fun updateStations(municipalityId: Int, stations: List<GasStation>) {
        _selection.value = SelectedMunicipality(municipalityId, stations)
    }
}
