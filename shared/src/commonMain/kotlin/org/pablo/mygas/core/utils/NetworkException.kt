package org.pablo.mygas.core.utils

class NetworkException(val error: NetworkError) : Exception("Network error: $error")
