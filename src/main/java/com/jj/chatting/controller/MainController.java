package com.jj.chatting.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Log4j2
public class MainController {

    @RequestMapping(value = {"/chat", "/"})
    public ModelAndView chat() {
        ModelAndView mnv = new ModelAndView();
        mnv.setViewName("chat");
        log.info("//////////// MainController Mapped //////////////");
        return mnv;
    }
}
