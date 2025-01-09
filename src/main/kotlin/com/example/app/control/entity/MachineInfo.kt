package com.example.app.control.entity

import org.springframework.web.socket.WebSocketSession

data class MachineInfo(
    var no : String = "",
    var msg : String = "",
    val ip : String = "",
    val session : WebSocketSession?,
)
