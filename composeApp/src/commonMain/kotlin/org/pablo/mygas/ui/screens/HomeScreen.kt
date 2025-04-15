package org.pablo.mygas.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import mygas.composeapp.generated.resources.Res
import mygas.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource
import org.pablo.mygas.data.ListaEESSPrecio
import org.pablo.mygas.ui.CustomDropDownMenu
import org.pablo.mygas.ui.ListaGasolineras
import org.pablo.mygas.ui.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onGasolineraClick: (ListaEESSPrecio) -> Unit,
    viewModel: HomeViewModel
) {
    Screen {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Res.string.app_name)) },
                    scrollBehavior = scrollBehavior

                )
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)

        ) { padding ->

            val isLoading = viewModel.state.isLoading
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    CustomDropDownMenu(
                        itemList = viewModel.municipios,
                        onValueChange = {
                            println("municipio seleccionado: ${it.municipio}")
                            println("idMunicipio seleccionado: ${it.idMunicipio}")
                            println("idMunicipio seleccionado: $it")
                            viewModel.getGasolinerasByMunicipio(it.idMunicipio)
                        },
                        municipio = viewModel.municipio
                    )
                    ListaGasolineras(
                        gasolineras = viewModel.gasolineras,
                        onClick = { onGasolineraClick(it) }
                    )
                }


            }
        }


    }

}