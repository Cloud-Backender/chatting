package com.jj.chatting.handler;

import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;

@Component
@Log4j2
public class SocketHandler extends TextWebSocketHandler {

    HashMap<String, WebSocketSession> map = new HashMap<>(); //웹소켓 세션을 담을 맵

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //메세지 발송
        String msg = message.getPayload(); //payload란 데이터를 나타냄.
        JSONObject jsonObject = jsonToObject(msg);
        for(String key : map.keySet()) {
            WebSocketSession webSocketSession = map.get(key);
            try {
                webSocketSession.sendMessage(new TextMessage(jsonObject.toJSONString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("/////////////////Message가 생성됐습니다. //////////////////");
    }


    @SuppressWarnings("unchecked")
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //소켓 연결
        map.put(session.getId(), session);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "getId");
        jsonObject.put("sessionId", session.getId());
        session.sendMessage(new TextMessage(jsonObject.toJSONString()));
        log.info("/////////////////소켓이 연결됐습니다. //////////////////");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //소켓 종료
        map.remove(session.getId());
        log.info("/////////////////소켓이 종료됐습니다. //////////////////");
    }

    private static JSONObject jsonToObject(String jsonStr) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
