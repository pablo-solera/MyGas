package org.pablo.mygas.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.pablo.mygas.ui.viewmodels.GasolineraFavoriteViewModel


@Composable
fun GasolineraFavoriteScreen(
    viewModel: GasolineraFavoriteViewModel
) {

    val state by viewModel.state.collectAsState()

    Checkbox(
        checked = state.isChecked,
        onCheckedChange = { viewModel.toggleChecked(it) },
    )
    if (state.isLoading) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(15.dp)
                .fillMaxSize()
        )
    } else {
        LazyColumn {
            state.municipios?.let {
                items(
                    it.size
                ) { index ->
                    val comunidad = state.municipios!![index]
                    Text(comunidad.municipio)
                }
            }

        }
        state.errorMessage?.let {
            Text(
                text = it.name,
                color = Color.Red,
                modifier = Modifier.fillMaxSize()
            )
        }
    }


}
