package com.sefford.kor.repositories.utils

import com.sefford.kor.errors.Error

fun <E : Error> emptyErrorHandler() = { ex: Throwable -> DefaultError(ex) as E }