package org.pablo.mygas.core.utils

import org.pablo.mygas.resources.Res
import org.pablo.mygas.resources.alcampo
import org.pablo.mygas.resources.atenoil
import org.pablo.mygas.resources.ballenoil
import org.pablo.mygas.resources.bp
import org.pablo.mygas.resources.carrefour
import org.pablo.mygas.resources.cepsa
import org.pablo.mygas.resources.fastfuel
import org.pablo.mygas.resources.galp
import org.pablo.mygas.resources.gasexpress
import org.pablo.mygas.resources.gasolinera
import org.pablo.mygas.resources.greengas
import org.pablo.mygas.resources.lavaplus
import org.pablo.mygas.resources.petroprix
import org.pablo.mygas.resources.plenergy
import org.pablo.mygas.resources.repsol
import org.pablo.mygas.resources.servifacil
import org.pablo.mygas.resources.shell
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
