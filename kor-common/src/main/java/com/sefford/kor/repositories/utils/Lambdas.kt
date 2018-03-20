package com.sefford.kor.repositories.utils

import arrow.core.Either
import com.sefford.kor.errors.Error
import com.sefford.kor.responses.Response
import java.lang.Exception

fun <R : Response> noop() = { response: R -> Either.right(response) }

fun <E : Error> emptyErrorHandler() = { ex: Exception -> DefaultError(ex) as E}