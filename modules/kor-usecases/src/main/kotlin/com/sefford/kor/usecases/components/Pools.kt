package com.sefford.kor.usecases.components

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newFixedThreadPoolContext

/**
 * Alternative Coroutine fix for processors with little cores
 *
 * As seen on:
 *
 * https://medium.com/@nickcapurso/kotlin-coroutines-on-android-things-i-wish-i-knew-at-the-beginning-c2f0b1f16cff
 */
val BackgroundPool: CoroutineDispatcher by lazy {
    val numProcessors = Runtime.getRuntime().availableProcessors()
    when {
        numProcessors <= 2 -> newFixedThreadPoolContext(2, "background")
        else -> Dispatchers.Default
    }
}
