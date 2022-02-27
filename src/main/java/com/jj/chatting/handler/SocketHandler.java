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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Log4j2
public class SocketHandler extends TextWebSocketHandler {

//    HashMap<String, WebSocketSession> map = new HashMap<>(); //웹소켓 세션을 담을 맵
    List<HashMap<String, Object>> roomListSessions = new ArrayList<>();
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //메세지 발송
        String msg = message.getPayload(); //payload란 데이터를 나타냄.
        JSONObject jsonObject = jsonToObject(msg);

        String roomNum = (String) jsonObject.get("roomNumber");
        HashMap<String, Object> temp = new HashMap<>();
        if(roomListSessions.size() > 0) {
            for (int i = 0; i < roomListSessions.size(); i++) {
                String roomNumber = (String) roomListSessions.get(i).get("roomNumber"); //세션리스트의 저장된 방번호를 가져와서
                if (roomNumber.equals(roomNumber)) { //같은값의 방이 존재한다면
                    temp = roomListSessions.get(i); //해당 방번호의 세션리스트의 존재하는 모든 object값을 가져온다.
                    break;
                }
            }
            //해당 방의 세션들만 찾아서 메시지를 발송해준다.
            for (String k : temp.keySet()) {
                if (k.equals("roomNumber")) { //다만 방번호일 경우에는 건너뛴다.
                    continue;
                }

                WebSocketSession wss = (WebSocketSession) temp.get(k);
                if (wss != null) {
                    try {
                        wss.sendMessage(new TextMessage(jsonObject.toJSONString()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
            log.info("/////////////////Message가 생성됐습니다. //////////////////");
    }


    @SuppressWarnings("unchecked")
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //소켓 연결
        super.afterConnectionEstablished(session);
        boolean flag = false;
        String url = session.getUri().toString();
        System.out.println(url);
        String roomNumber = url.split("/chating/")[1];
        int idx = roomListSessions.size(); //방의 사이즈를 조사한다.
        if(roomListSessions.size() > 0) {
            for(int i=0; i<roomListSessions.size(); i++) {
                String rN = (String) roomListSessions.get(i).get("roomNumber");
                if(rN.equals(roomNumber)) {
                    flag = true;
                    idx = i;
                    break;
                }
            }
        }

        if(flag) { //존재하는 방이라면 세션만 추가한다.
            HashMap<String, Object> map = roomListSessions.get(idx);
            map.put(session.getId(), session);
        }else { //최초 생성하는 방이라면 방번호와 세션을 추가한다.
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("roomNumber", roomNumber);
            map.put(session.getId(), session);
            roomListSessions.add(map);
        }

        //세션등록이 끝나면 발급받은 세션ID값의 메시지를 발송한다.
        JSONObject obj = new JSONObject();
        obj.put("type", "getId");
        obj.put("sessionId", session.getId());
        session.sendMessage(new TextMessage(obj.toJSONString()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //소켓 종료
        if(roomListSessions.size() > 0) { //소켓이 종료되면 해당 세션값들을 찾아서 지운다.
            for(int i=0; i<roomListSessions.size(); i++) {
                roomListSessions.get(i).remove(session.getId());
            }
        }
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
