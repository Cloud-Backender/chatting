package com.jj.chatting.controller;

import com.jj.chatting.model.RoomDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Log4j2
public class MainController {

    List<RoomDto> roomList = new ArrayList<>();
    static int roomNumber = 0;

    @RequestMapping( {"/chat"})
    public ModelAndView chat() {
        ModelAndView mnv = new ModelAndView();
        mnv.setViewName("chat");
        log.info("//////////// Chat View Mapped //////////////");
        return mnv;
    }

    @RequestMapping( {"/room", "/"})
    public ModelAndView room() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("room");
        log.info("//////////// Room View Mapped //////////////");
        return mv;
    }

    @RequestMapping("/createRoom")
    public @ResponseBody
    List<RoomDto> createRoom(@RequestParam HashMap<Object, Object> params){
        String roomName = (String) params.get("roomName");
        if(roomName != null && !roomName.trim().equals("")) {
            RoomDto room = new RoomDto();
            room.setRoomNumber(++roomNumber);
            room.setRoomName(roomName);
            roomList.add(room);
        }
        log.info("////////////{} Room Created //////////////", roomList.toArray().length);
        return roomList;
    }

    @RequestMapping("/getRoom")
    public @ResponseBody List<RoomDto> getRoom(@RequestParam HashMap<Object, Object> params){
        log.info("//////////// retrieve Room List //////////////");
        return roomList;
    }
    @RequestMapping("/moveChating")
    public ModelAndView chating(@RequestParam HashMap<Object, Object> params) {
        ModelAndView mv = new ModelAndView();
        int roomNumber = Integer.parseInt((String) params.get("roomNumber"));

        List<RoomDto> new_list = roomList.stream().filter(o->o.getRoomNumber()==roomNumber).collect(Collectors.toList());
        if(new_list != null && new_list.size() > 0) {
            mv.addObject("roomName", params.get("roomName"));
            mv.addObject("roomNumber", params.get("roomNumber"));
            mv.setViewName("chat");
        }else {
            mv.setViewName("room");
        }
        log.info("//////////// move to chatting room //////////////");
        return mv;
    }


}
