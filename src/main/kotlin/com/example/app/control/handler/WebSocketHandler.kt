package com.example.app.control.handler

import com.example.app.control.entity.Command
import com.example.app.control.entity.MachineInfo
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.io.IOException
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@Component
class WebSocketHandler : TextWebSocketHandler() {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
        private val objectMapper = ObjectMapper().registerKotlinModule()
        // sessions 保存WebSocket会话，key为sessionId
        private val sessionsMap = ConcurrentHashMap<String, MachineInfo>()
        // infoList 保存session客户端的信息
//        private val infoList = ConcurrentHashMap<String, >
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val sessionId = session.id
        sessionsMap[sessionId] = MachineInfo(ip = session.remoteAddress.toString(), session = session)
        log.info("Client connected: $sessionId . IP Address: local-${session.localAddress} remote-${session.remoteAddress}")
        session.sendMessage(TextMessage("Connected Server ${LocalDateTime.now()}"))
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        // 处理收到的消息
        val payload = message.payload
        log.info("Received message: $payload from ${session.id}")
        // 处理指令
        try {
            val command = objectMapper.readValue(payload, object : TypeReference<Command<MachineInfo>>() {})
            val machineInfo : MachineInfo? = sessionsMap[session.id]
            if (machineInfo != null) {
                machineInfo.msg = command.cmd.msg
                machineInfo.no = command.cmd.no
            }
            session.sendMessage(TextMessage("Server Update Success: ${machineInfo.toString()}"))
        } catch (e : Exception) {
            e.printStackTrace()
            // 发送响应消息
            session.sendMessage(TextMessage("Server received Plain Text: $payload"))
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessionsMap.remove(session.id)
        log.info("Client disconnected: ${session.id}")
    }

    // 广播消息给所有连接的客户端
    fun broadcast(message: String) {
        sessionsMap.values.forEach { item ->
            try {
                item.session?.sendMessage(TextMessage(message))
            } catch (e: IOException) {
                log.error("Error broadcasting message: ${e.message}")
            }
        }
    }

    fun getAllClient() {

    }
}