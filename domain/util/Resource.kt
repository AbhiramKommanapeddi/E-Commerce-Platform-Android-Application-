package com.tutedude.ecommerce.domain.util

/**
 * A generic class that holds a value with its loading status.
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(val isLoading: Boolean = true, data: T? = null) : Resource<T>(data)
}
