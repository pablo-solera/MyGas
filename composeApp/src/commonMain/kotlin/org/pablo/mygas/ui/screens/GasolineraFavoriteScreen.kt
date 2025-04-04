package org.pablo.mygas.ui.screens

import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.pablo.mygas.ui.viewmodels.GasolineraDetailViewModel
import org.pablo.mygas.ui.viewmodels.GasolineraFavoriteViewModel

data class GasolineraDetailState(
    val isChecked: Boolean = false,
)

@Composable
fun GasolineraFavoriteScreen(
    viewModel: GasolineraFavoriteViewModel
) {
    //val viewmodel = koinViewModel<GasolineraDetailViewModel>()
    val state by viewModel.state.collectAsState()
    Checkbox(
        checked = state.isChecked,
        onCheckedChange = { viewModel.toggleChecked(it) },
    )

}
