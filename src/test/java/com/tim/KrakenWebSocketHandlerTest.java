package com.tim;

import com.tim.websocket.KrakenWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;

class KrakenWebSocketHandlerTest {

  @Test
  void handleMessage() throws Exception {
    var handler = new KrakenWebSocketHandler();
    handler.handleMessage(null, new TextMessage("[300,{\"a\":[[\"15317.40000\",\"0.49499931\",\"1604767055.453980\"]],\"c\":\"154867001\"},\"book-10\",\"XBT/USD\"]\n"));
    handler.handleMessage(null, new TextMessage("[300,{\"a\":[[\"15317.40000\",\"0.00000000\",\"1604767055.027836\"],[\"15320.00000\",\"0.25962500\",\"1604767016.849566\",\"r\"]],\"c\":\"2191247167\"},\"book-10\",\"XBT/USD\"]\n"));
  }
}