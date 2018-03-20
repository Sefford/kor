package com.sefford.kor.repositories.utils

import com.sefford.common.interfaces.Postable
import com.sefford.kor.errors.Error
import com.sefford.kor.responses.Response

class DummyPostable : Postable {
    val events: MutableList<Any> = mutableListOf()
    var postedError = false
    var postedResponse = false

    override fun post(event: Any) {
        events.add(event)
        postedError = event is Error
        postedResponse = event is Response
    }

    fun hasReceivedAPost() = events.isNotEmpty()

}