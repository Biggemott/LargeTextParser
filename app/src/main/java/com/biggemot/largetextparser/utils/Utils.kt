package com.biggemot.largetextparser.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber


/**
 * Process the first call right away and then skip subsequent calls for skipMs
 */
fun <T> throttleFirst(
    coroutineScope: CoroutineScope,
    skipMs: Long = 300L,
    destinationFunction: (T) -> Unit
): (T) -> Unit {
    var throttleJob: Job? = null
    return { param: T ->
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                destinationFunction(param)
                delay(skipMs)
            }
        } else {
            Timber.d("throttled")
        }
    }
}