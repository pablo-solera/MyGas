package org.pablo.mygas.ui.components.tilemap

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image
import java.io.File
import java.util.concurrent.TimeUnit

data class TileKey(val zoom: Int, val x: Int, val y: Int)

/**
 * Loads OpenStreetMap raster tiles following the OSM tile usage policy: an identifying
 * User-Agent, at most two concurrent downloads and a local disk cache.
 */
class TileRepository(
    private val cacheDirectory: File = File(System.getProperty("user.home"), ".mygas/tiles"),
    private val tileUrl: (TileKey) -> String = { "https://tile.openstreetmap.org/${it.zoom}/${it.x}/${it.y}.png" },
) {
    private val downloads = Semaphore(MAX_CONCURRENT_DOWNLOADS)

    private val client by lazy {
        HttpClient(CIO) {
            install(UserAgent) { agent = USER_AGENT }
            install(HttpTimeout) {
                connectTimeoutMillis = 8_000
                requestTimeoutMillis = 15_000
            }
        }
    }

    suspend fun load(key: TileKey): ImageBitmap? = withContext(Dispatchers.IO) {
        val file = File(cacheDirectory, "${key.zoom}/${key.x}/${key.y}.png")
        val bytes = readFresh(file) ?: download(key, file) ?: file.takeIf { it.exists() }?.readBytes()
        bytes?.let { runCatching { Image.makeFromEncoded(it).toComposeImageBitmap() }.getOrNull() }
    }

    private fun readFresh(file: File): ByteArray? {
        if (!file.exists()) return null
        val ageMs = System.currentTimeMillis() - file.lastModified()
        return if (ageMs < TimeUnit.DAYS.toMillis(CACHE_DAYS)) file.readBytes() else null
    }

    private suspend fun download(key: TileKey, target: File): ByteArray? = downloads.withPermit {
        try {
            val response = client.get(tileUrl(key))
            if (!response.status.isSuccess()) return@withPermit null
            val bytes = response.bodyAsBytes()
            target.parentFile?.mkdirs()
            target.writeBytes(bytes)
            bytes
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            null
        }
    }

    private companion object {
        const val MAX_CONCURRENT_DOWNLOADS = 2
        const val CACHE_DAYS = 7L
        const val USER_AGENT = "MyGas-Desktop/1.0 (Compose Multiplatform fuel price app)"
    }
}
