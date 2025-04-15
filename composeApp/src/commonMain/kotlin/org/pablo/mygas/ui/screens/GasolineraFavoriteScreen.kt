package org.pablo.mygas.ui.screens

import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.pablo.mygas.ui.viewmodels.GasolineraFavoriteViewModel


@Composable
fun GasolineraFavoriteScreen(
    viewModel: GasolineraFavoriteViewModel
) {

    val gasolineraState by viewModel.state.collectAsState()


    Checkbox(
        checked = gasolineraState.isChecked,
        onCheckedChange = { viewModel.toggleChecked(it) },
    )

}
