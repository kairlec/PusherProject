package com.kairlec.pusher.openapi

class CustomEvent : Event {
    private lateinit var api: API

    override fun onCreated(api: API) {
        this.api = api
    }


}

class EventTest {
    fun test() {

    }
}