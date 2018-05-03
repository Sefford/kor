package com.sefford.kor.usecases.components

interface PerformanceModule {

    val name: String

    fun start(traceId : String = name)

    fun end(traceId : String = name)
}