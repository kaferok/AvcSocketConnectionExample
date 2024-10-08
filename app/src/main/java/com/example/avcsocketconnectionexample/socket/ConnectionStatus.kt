package com.example.avcsocketconnectionexample.socket

enum class ConnectionStatus(val label: String) {
    CONNECTED("Active"),
    DISCONNECTED("Inactive");

    companion object {

        fun getStatus(hasUser: Boolean): ConnectionStatus = if (hasUser) CONNECTED else DISCONNECTED
    }
}