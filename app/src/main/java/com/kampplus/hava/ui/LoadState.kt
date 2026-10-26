package com.kampplus.hava.ui

sealed interface LoadState<out T> {
    data object Loading : LoadState<Nothing>
    data class Success<out T>(val data: T) : LoadState<T>
    data object Empty : LoadState<Nothing>
    /** [isNetwork] true ise arayüz, sistem mesajı yerine yerelleştirilmiş "bağlantı yok" metnini gösterir. */
    data class Error(val message: String, val isNetwork: Boolean = false) : LoadState<Nothing>
}
