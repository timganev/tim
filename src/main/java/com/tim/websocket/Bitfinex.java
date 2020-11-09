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
public class Bitfinex implements Exchange {


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
    if (root.isArray() && root.size() > 1 && root.get(1).isArray()) {
      JsonNode node = root.get(1);

      Double price = node.get(0).asDouble();
      Long count = node.get(1).asLong();
      Double amount = node.get(2).asDouble();

//      System.out.println(node);

      if (count != null && price != null && amount != null) {

        if (count > 0) {

          if (amount > 0) {
            bidBitfinex.put(price, amount);
            bidAgreggate.put(price, amount);
          } else {
            askBitfinex.put(price, amount * -1);
            askAgreggate.put(price, amount * -1);
          }

        } else {
          askBitfinex.remove(price);
          bidBitfinex.remove(price);

          askAgreggate.remove(price);
          if(askKraken.containsKey(price)){
            askAgreggate.put(price, askKraken.get(price));
          }

          bidAgreggate.remove(price);
          if(bidKraken.containsKey(price)){
            bidAgreggate.put(price, bidKraken.get(price));
          }
        }

        printBook();

      }
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
