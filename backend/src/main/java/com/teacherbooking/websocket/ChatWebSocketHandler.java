package com.teacherbooking.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teacherbooking.entity.Message;
import com.teacherbooking.security.JwtUtil;
import com.teacherbooking.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天WebSocket处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final JwtUtil jwtUtil;
    private final AppointmentService appointmentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = extractTokenFromQuery(session);
        if (token == null || !jwtUtil.validateToken(token)) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        sessions.put(userId, session);
        log.info("WebSocket连接建立: userId={}", userId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String token = extractTokenFromQuery(session);
        if (token == null) {
            return;
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        String payload = message.getPayload();

        try {
            Map<String, Object> msgData = objectMapper.readValue(payload, Map.class);
            Long appointmentId = Long.valueOf(msgData.get("appointmentId").toString());
            String content = msgData.get("content").toString();
            String type = msgData.getOrDefault("type", "TEXT").toString();

            Message savedMessage = appointmentService.sendMessage(appointmentId, content, type);

            sendToUser(savedMessage.getReceiverId(), objectMapper.writeValueAsString(savedMessage));

        } catch (Exception e) {
            log.error("处理WebSocket消息失败: {}", e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String token = extractTokenFromQuery(session);
        if (token != null) {
            Long userId = jwtUtil.getUserIdFromToken(token);
            sessions.remove(userId);
            log.info("WebSocket连接关闭: userId={}", userId);
        }
    }

    public void sendToUser(Long userId, String message) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error("发送WebSocket消息失败: userId={}, error={}", userId, e.getMessage());
            }
        }
    }

    private String extractTokenFromQuery(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2 && "token".equals(pair[0])) {
                    return pair[1];
                }
            }
        }
        return null;
    }
}
