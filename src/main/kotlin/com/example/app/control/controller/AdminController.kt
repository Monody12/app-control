package com.example.app.control.controller

import com.example.app.control.entity.ApiResponse
import com.example.app.control.handler.WebSocketHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class AdminController {

    @Autowired
    lateinit var webSocketHandler : WebSocketHandler

    @RequestMapping("/broadcast")
    fun broadcast(text : String) : ApiResponse<Nothing> {
        webSocketHandler.broadcast(text)
        return ApiResponse(code = 200, msg = "成功")
    }

}