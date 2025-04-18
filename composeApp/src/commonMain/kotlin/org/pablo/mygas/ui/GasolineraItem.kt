package org.pablo.mygas.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mygas.composeapp.generated.resources.Res
import mygas.composeapp.generated.resources.gasolinera
import org.jetbrains.compose.resources.painterResource
import org.pablo.mygas.data.ListaEESSPrecio
import org.pablo.mygas.utils.GasStationImage


@Composable
fun GasolineraItem(
    gasolinera: ListaEESSPrecio,
    onClick: (ListaEESSPrecio) -> Unit
) {
    Row(
        modifier = Modifier.clickable { onClick(gasolinera) }
            .padding(12.dp, 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Image(
                painterResource(GasStationImage().getImage(gasolinera.rotulo.split(" ").first())), contentDescription = null,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(16.dp))
                    .size(50.dp)
                    .aspectRatio(1f)
            )
            Column {
                Text(
                    text = gasolinera.rotulo,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,

                    )
            }
        }
        Text(
            text = "${gasolinera.precioGasolina95E5} €/l",
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            modifier = Modifier.padding(8.dp)
        )
    }
}