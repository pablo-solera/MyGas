package org.pablo.mygas.core.utils

import mygas.composeapp.generated.resources.Res
import mygas.composeapp.generated.resources.alcampo
import mygas.composeapp.generated.resources.atenoil
import mygas.composeapp.generated.resources.ballenoil
import mygas.composeapp.generated.resources.bp
import mygas.composeapp.generated.resources.carrefour
import mygas.composeapp.generated.resources.cepsa
import mygas.composeapp.generated.resources.fastfuel
import mygas.composeapp.generated.resources.galp
import mygas.composeapp.generated.resources.gasexpress
import mygas.composeapp.generated.resources.gasolinera
import mygas.composeapp.generated.resources.greengas
import mygas.composeapp.generated.resources.lavaplus
import mygas.composeapp.generated.resources.petroprix
import mygas.composeapp.generated.resources.plenergy
import mygas.composeapp.generated.resources.repsol
import mygas.composeapp.generated.resources.servifacil
import mygas.composeapp.generated.resources.shell
import org.jetbrains.compose.resources.DrawableResource

object GasStationImage {

     fun getImage(gasStation: String): DrawableResource {
        return when (gasStation.lowercase()) {
            "repsol" -> Res.drawable.repsol
            "cepsa" -> Res.drawable.cepsa
            "moeve" -> Res.drawable.cepsa
            "bp" -> Res.drawable.bp
            "galp" -> Res.drawable.galp
            "alcampo" -> Res.drawable.alcampo
            "atenoil" -> Res.drawable.atenoil
            "carrefour" -> Res.drawable.carrefour
            "shell" -> Res.drawable.shell
            "petroprix" -> Res.drawable.petroprix
            "ballenoil" -> Res.drawable.ballenoil
            "fastfuel" -> Res.drawable.fastfuel
            "gasexpress" -> Res.drawable.gasexpress
            "greengas" -> Res.drawable.greengas
            "lavaplus" -> Res.drawable.lavaplus
            "plenergy" -> Res.drawable.plenergy
            "servifacil" -> Res.drawable.servifacil
            else -> Res.drawable.gasolinera
        }
    }
}
