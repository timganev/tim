package com.tim.websocket;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.DecimalFormat;
import java.util.HashMap;
import lombok.Data;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Data
public class Kraken implements WebSocketHandler {

  HashMap<Double, HashMap<Long, Double>> askMap = new HashMap<>();
  HashMap<Double, HashMap<Long, Double>> bidMap = new HashMap<>();

  public static final String MESSAGE = "{ \"event\": \"subscribe\",  \"pair\": [\"XBT/USD\"],  \"subscription\": {\"name\": \"book\"} }";

  ObjectMapper objectMapper = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    session.sendMessage(new TextMessage(MESSAGE));
  }

  @Override
  public void handleMessage(WebSocketSession session, WebSocketMessage<?> message)
      throws Exception {
    System.out.println(message.getPayload());

//    JsonNode root = objectMapper.readTree(message.getPayload().toString());
//    if (root.isArray()) {
//      TickerUpdate update = objectMapper.treeToValue(root.get(1), TickerUpdate.class);
//      System.out.println(update);
//    }
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception)
      throws Exception {
    exception.printStackTrace();
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
      throws Exception {
    System.out.println("Closed");
  }

  @Override
  public boolean supportsPartialMessages() {
    return false;
  }
}
