package org.pablo.mygas.ui.state

import org.pablo.mygas.core.utils.NetworkError

sealed interface Loadable<out T> {
    data object Idle : Loadable<Nothing>
    data object Loading : Loadable<Nothing>
    data class Content<T>(val value: T) : Loadable<T>
    data class Failed(val error: NetworkError) : Loadable<Nothing>
}
