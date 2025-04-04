package org.pablo.mygas.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.pablo.mygas.data.ListaEESSPrecio

@Composable
fun ListaGasolineras(
    gasolineras: List<ListaEESSPrecio>,
    onClick: (ListaEESSPrecio) -> Unit
) {
    LazyColumn(
        //columns = GridCells.Adaptive(100.dp),
        // contentPadding = PaddingValues(8.dp),
        // horizontalAlignment = Alignment.CenterHorizontally,
        // horizontalArrangement = Arrangement.spacedBy(4.dp),
        // verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(
            count = gasolineras.count(),
            itemContent = { index -> GasolineraItem(
                gasolineras[index],
                onClick = {onClick(gasolineras[index])},
            ) },
            key = { index -> gasolineras[index].IDEESS }
        )
    }
}
