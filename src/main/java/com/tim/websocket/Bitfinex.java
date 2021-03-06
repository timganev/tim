package com.tim.websocket;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tim.service.OrderBookService;
import java.util.ArrayList;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Data
@Service
public class Bitfinex implements Exchange {


  OrderBookService orderBookService;

  @Autowired
  public Bitfinex(OrderBookService orderBookService) {
    this.orderBookService = orderBookService;
  }

  public static final String MESSAGE = "  { \"event\": \"subscribe\", \"channel\": \"book\", \"symbol\": \"tBTCUSD\"}";

  ObjectMapper objectMapper = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    session.sendMessage(new TextMessage(MESSAGE));
  }

  @Override
  public void handleMessage(WebSocketSession session, WebSocketMessage<?> message)
      throws Exception {
    JsonNode root = objectMapper.readTree(message.getPayload().toString());
    orderBookService.updateBitfinex(root);
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception)
      throws Exception {
    exception.printStackTrace();
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
      throws Exception {
    String str = "mmm k k";
    String[] tokens = str.split("!|\\,|\\?|\\.|\\_|\\'|\\@");

    ArrayList<String> result = new ArrayList();

    for (int i = 0; i < tokens.length; i++) {
      if (!tokens[i].equals(" ")) {
        result.add(tokens[i]);
      }
    }
    System.out.println(result.size());
    for (int i = 0; i <result.size() ; i++) {
      System.out.println(result.get(i));
    }



    System.out.println("Closed");

  }

  @Override
  public boolean supportsPartialMessages() {
    return false;
  }
}
