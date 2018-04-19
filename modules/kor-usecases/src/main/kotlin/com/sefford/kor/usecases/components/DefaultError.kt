package com.sefford.kor.usecases.components

import com.sefford.kor.usecases.components.Error

class DefaultError(val exception: Throwable) : Error {

    override val statusCode: Int
        get() = -1
    override val userError: String
        get() = ""
    override val message: String
        get() = ""
    override val loggable: Boolean
        get() = true

}