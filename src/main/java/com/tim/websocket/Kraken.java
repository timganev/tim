package com.tim.websocket;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Data
public class Kraken implements Exchange {

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

    JsonNode root = objectMapper.readTree(message.getPayload().toString());
    if (root.isArray() && root.size() > 1) {
      JsonNode node = root.get(1);
      JsonNode askArr = node.get("a");
      JsonNode bidArr = node.get("b");

      if (askArr != null && askArr.size() > 0) {

        askArr.forEach(order -> {
          Double price = order.get(0).asDouble();
          Double amount = order.get(1).asDouble();
          if (amount > 0) {
            askKraken.put(price, amount);
            askAgreggate.put(price, amount);
          } else {
            askKraken.remove(price);
            askAgreggate.remove(price);
          }
        });

      }

      if (bidArr != null && bidArr.size() > 0) {

        bidArr.forEach(order -> {
          Double price = order.get(0).asDouble();
          Double amount = order.get(1).asDouble();
          if (amount > 0) {
            bidKraken.put(price, amount);
            bidAgreggate.put(price, amount);
          } else {
            bidKraken.remove(price);
            bidAgreggate.remove(price);
          }
        });

      }
      printBook();
      //        System.out.println("ASK: " + askKraken.size());
      //        System.out.println("BID: " + bidKraken.size());
    }
  }

  public void printBook() {
    System.out.println();
    System.out.println("Order book");
    System.out.println("asks:");
    askAgreggate.entrySet().forEach(entry -> {
      System.out.println(df.format(entry.getKey()) + "  |  " + df.format(entry.getValue()));
    });
    if (askAgreggate.size() > 0) {
      System.out.println();
      System.out.println(
          "best ask: " + df.format(askAgreggate.lastEntry().getKey()) + "  |  " + df
              .format(askAgreggate.lastEntry()
                  .getValue()));
    }

    if (bidAgreggate.size() > 0) {
      System.out.println(
          "best bid: " + df.format(bidAgreggate.firstEntry().getKey()) + "  |  " + df
              .format(bidAgreggate.firstEntry()
                  .getValue()));
    }
    System.out.println();
    System.out.println("bids:");
    bidAgreggate.entrySet().forEach(entry -> {
      System.out.println(df.format(entry.getKey()) + "  |  " + df.format(entry.getValue()));
    });
    System.out.println("=================================");
    System.out.println("Aggregate asks: " + askAgreggate.size());
    System.out.println("Aggregate bids: " + bidAgreggate.size());
    System.out.println("=================================");
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
