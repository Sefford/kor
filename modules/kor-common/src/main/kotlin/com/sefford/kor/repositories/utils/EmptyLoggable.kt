package com.sefford.kor.repositories.utils

import com.sefford.common.interfaces.Loggable

class EmptyLoggable : Loggable {

    override fun printPerformanceLog(tag: String?, delegateName: String?, start: Long) {

    }

    override fun e(tag: String?, message: String?, exception: Throwable?) {

    }

    override fun d(tag: String?, message: String?) {

    }

}