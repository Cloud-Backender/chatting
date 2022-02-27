package com.jj.chatting.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;

@Component
public class SocketHandler extends TextWebSocketHandler {

    HashMap<String, WebSocketSession> map = new HashMap<>(); //웹소켓 세션을 담을 맵

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //메세지 발송
        String msg = message.getPayload(); //payload란 데이터를 나타냄.
        for(String key : map.keySet()) {
            WebSocketSession webSocketSession = map.get(key);
            try {
                webSocketSession.sendMessage(new TextMessage(msg));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //소켓 연결
        map.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //소켓 종료
        map.remove(session.getId());
    }
}
