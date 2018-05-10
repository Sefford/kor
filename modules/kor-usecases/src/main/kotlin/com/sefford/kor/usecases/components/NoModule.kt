package com.sefford.kor.usecases.components

object NoModule : PerformanceModule {

    override val name: String
        get() = ""

    override fun start(traceId: String) {
        // Empty
    }

    override fun end(traceId: String) {
        // Empty
    }
}