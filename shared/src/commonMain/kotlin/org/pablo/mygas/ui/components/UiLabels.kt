package org.pablo.mygas.ui.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.domain.models.FuelType
import org.pablo.mygas.resources.Res
import org.pablo.mygas.resources.error_no_internet
import org.pablo.mygas.resources.error_not_found
import org.pablo.mygas.resources.error_serialization
import org.pablo.mygas.resources.error_server
import org.pablo.mygas.resources.error_timeout
import org.pablo.mygas.resources.error_too_many_requests
import org.pablo.mygas.resources.error_unknown
import org.pablo.mygas.resources.fuel_biodiesel
import org.pablo.mygas.resources.fuel_diesel
import org.pablo.mygas.resources.fuel_diesel_premium
import org.pablo.mygas.resources.fuel_gasoline_95
import org.pablo.mygas.resources.fuel_gasoline_98
import org.pablo.mygas.resources.fuel_glp
import org.pablo.mygas.resources.fuel_hvo

@Composable
fun FuelType.label(): String = stringResource(
    when (this) {
        FuelType.GASOLINE_95 -> Res.string.fuel_gasoline_95
        FuelType.GASOLINE_98 -> Res.string.fuel_gasoline_98
        FuelType.DIESEL -> Res.string.fuel_diesel
        FuelType.DIESEL_PREMIUM -> Res.string.fuel_diesel_premium
        FuelType.BIO_DIESEL -> Res.string.fuel_biodiesel
        FuelType.GLP -> Res.string.fuel_glp
        FuelType.HVO -> Res.string.fuel_hvo
    }
)

@Composable
fun NetworkError.message(): String = stringResource(
    when (this) {
        NetworkError.NO_INTERNET -> Res.string.error_no_internet
        NetworkError.REQUEST_TIMEOUT -> Res.string.error_timeout
        NetworkError.SERVER_ERROR -> Res.string.error_server
        NetworkError.NOT_FOUND -> Res.string.error_not_found
        NetworkError.TOO_MANY_REQUESTS -> Res.string.error_too_many_requests
        NetworkError.SERIALIZATION -> Res.string.error_serialization
        NetworkError.UNAUTHORIZED,
        NetworkError.FORBIDDEN,
        NetworkError.CONFLICT,
        NetworkError.PAYLOAD_TOO_LARGE,
        NetworkError.UNKNOWN -> Res.string.error_unknown
    }
)
