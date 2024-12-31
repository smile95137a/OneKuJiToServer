package com.one.controller;

import com.one.frontend.model.GachaMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GachaWebSocketController {

    @MessageMapping("/draw")
    @SendTo("/topic/lottery")
    public GachaMessage sendLotteryMessage(GachaMessage message) throws Exception {
        System.out.println("Received message: " + message);
        return message;
    }
}
