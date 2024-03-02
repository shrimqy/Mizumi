package com.dokja.mizumi.utils

import com.dokja.mizumi.data.Response
import kotlin.coroutines.cancellation.CancellationException

suspend inline fun <T> tryAsResponse(crossinline call: suspend () -> T): Response<T> = try {
    Response.Success(call())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Response.Error(e.message ?: "", e)
}

inline fun <T> runCatchingAsResponse(crossinline call: () -> T): Response<T> = try {
    Response.Success(call())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Response.Error(e.message ?: "", e)
}