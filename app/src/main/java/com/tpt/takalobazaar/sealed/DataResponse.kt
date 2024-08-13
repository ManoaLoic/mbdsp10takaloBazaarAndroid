package com.tpt.takalobazaar.sealed

sealed class DataResponse<T>(
    var data: T? = null,
    var error: com.tpt.takalobazaar.sealed.Error? = null,
) {
    class Success<T>(data: T) : DataResponse<T>(data = data)
    class Error<T>(error: com.tpt.takalobazaar.sealed.Error) : DataResponse<T>(error = error)
}