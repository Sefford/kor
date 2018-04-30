package com.sefford.kor.usecases.components

fun <E : Error> emptyErrorHandler() = { ex: Throwable -> DefaultError(ex) as E }
