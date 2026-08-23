package org.pablo.mygas.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.swmansion.kmpmaps.core.CameraPosition
import com.swmansion.kmpmaps.core.ClusterSettings
import com.swmansion.kmpmaps.core.Coordinates
import com.swmansion.kmpmaps.core.Map
import com.swmansion.kmpmaps.core.MapConfiguration
import com.swmansion.kmpmaps.core.MapType
import com.swmansion.kmpmaps.core.MapUISettings
import com.swmansion.kmpmaps.core.Marker
import org.pablo.mygas.domain.models.map.MapCoordinate
import org.pablo.mygas.domain.models.map.MapProperties
import org.pablo.mygas.domain.models.map.MapState
import org.pablo.mygas.BuildKonfig
import dev.datlag.kcef.KCEF
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private object KcefLoader {
    val initialized = CompletableDeferred<Unit>()
    val started = AtomicBoolean(false)
    val configured = AtomicBoolean(false)
}


@Composable
actual fun Map(
    modifier: Modifier,
    state: MapState,
    properties: MapProperties,
    onMapClick: (MapCoordinate) -> Unit,
    onMapLongClick: (MapCoordinate) -> Unit,
    content: @Composable (() -> Unit)
) {
    var isReady by remember { mutableStateOf(KcefLoader.initialized.isCompleted) }
    var error by remember { mutableStateOf<Throwable?>(null) }

    LaunchedEffect(Unit) {
        if (KcefLoader.started.compareAndSet(false, true)) {
            withContext(Dispatchers.IO) {
                KCEF.init(
                    builder = {
                        installDir(File(System.getProperty("user.home"), ".mygas/kcef-bundle"))
                        progress { onInitialized { KcefLoader.initialized.complete(Unit) } }
                        settings { noSandbox = true }
                    },
                    onError = { throwable ->
                        KcefLoader.initialized.completeExceptionally(
                            throwable ?: IllegalStateException("KCEF initialization failed")
                        )
                    }
                )
            }
        }

        try {
            KcefLoader.initialized.await()
            isReady = true
        } catch (throwable: CancellationException) {
            throw throwable
        } catch (throwable: Throwable) {
            error = throwable
        }
    }

    LaunchedEffect(isReady) {
        if (isReady && KcefLoader.configured.compareAndSet(false, true)) {
            MapConfiguration.initialize(
                googleMapsApiKey = BuildKonfig.MAPS_API_KEY
            )
        }
    }

    if (!isReady) {
        Text(error?.let { "Map initialization failed: ${it.message}" } ?: "Initializing Map...")
        return
    }

    val markers = remember(state.markers) {
        state.markers.map { marker ->
            Marker(
                contentId = marker.id,
                coordinates = Coordinates(
                    latitude = marker.position.latitude,
                    longitude = marker.position.longitude
                ),
                title = marker.title,
            )
        }
    }

    Map(
        clusterSettings = ClusterSettings(
            enabled = true,
        ),
        modifier = modifier,
        uiSettings = MapUISettings(
            compassEnabled = true,
            myLocationButtonEnabled = true
        ),
        properties = com.swmansion.kmpmaps.core.MapProperties(
            mapType = MapType.NORMAL,
            isBuildingEnabled = properties.isBuildingEnabled,
            isMyLocationEnabled = properties.isMyLocationEnabled,
            isTrafficEnabled = properties.isTrafficEnabled,
        ),
        cameraPosition = CameraPosition(
            coordinates = Coordinates(
                latitude = state.cameraPosition.coordinates.latitude,
                longitude = state.cameraPosition.coordinates.longitude,
            ),
            zoom = state.cameraPosition.zoom
        ),
        markers = markers,
        onMarkerClick = { },
        onMapClick = {
            onMapClick(MapCoordinate(it.latitude, it.longitude))
        },
        onMapLongClick = {
            onMapLongClick(MapCoordinate(it.latitude, it.longitude))
        },


    )
}
