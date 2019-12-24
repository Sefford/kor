package com.sefford.kor.usecases.test.utils

import com.sefford.common.interfaces.Postable

/**
 * Test double for Postable arguments.
 *
 * Keeps a list of posted events.
 */
class TestPostable : Postable {

    internal val events: MutableList<Any> = mutableListOf()

    override fun post(event: Any) {
        events.add(event)
    }

    fun messagesReceived(): Int {
        return events.size
    }

    fun message(i: Int): Any {
        return events[i]
    }

    fun noMessagesReceived(): Boolean {
        return events.isEmpty()
    }

}