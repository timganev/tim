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
            askAggregate.put(price, amount);
          } else {
            askKraken.remove(price);

            askAggregate.remove(price);
            if(askBitfinex.containsKey(price)){
              askAggregate.put(price, askBitfinex.get(price));
            }
          }
        });

      }

      if (bidArr != null && bidArr.size() > 0) {

        bidArr.forEach(order -> {
          Double price = order.get(0).asDouble();
          Double amount = order.get(1).asDouble();
          if (amount > 0) {
            bidKraken.put(price, amount);
            bidAggregate.put(price, amount);
          } else {
            bidKraken.remove(price);

            bidAggregate.remove(price);
            if(bidBitfinex.containsKey(price)){
              bidAggregate.put(price, bidBitfinex.get(price));
            }
          }
        });

      }
//      printBook();
    }
  }

  public void printBook() {
    StringBuilder str = new StringBuilder();
    str.append("\n");

    str.append("Order book\n");
    str.append("asks:\n");

    askAggregate.entrySet().forEach(entry -> {
      str.append(df.format(entry.getKey()) + "  |  " + df.format(entry.getValue()));
      if (askKraken.containsKey(entry.getKey()) && askBitfinex.containsKey(entry.getKey())) {
        str.append("  Aggregate\n");
      } else if (askKraken.containsKey(entry.getKey())) {
        str.append("  Kraken\n");
      } else {
        str.append("  Bitfinex\n");
      }

    });

    if (bidAggregate.size() > 0 && askAggregate.size() > 0) {
      str.append("\n");
      str.append("best ask: " + df.format(askAggregate.lastEntry().getKey()) + "  |  " + df
          .format(askAggregate.lastEntry().getValue()) + "\n");

      // Check for Arbitrage
      if (askAggregate.lastEntry().getKey() > bidAggregate.firstEntry().getKey()) {
        str.append("Arbitrage is not possible at the moment\n");
      } else {
        str.append("Arbitrage Opportunity !!!\n");
      }
      str.append("best bid: " + df.format(bidAggregate.firstEntry().getKey()) + "  |  " + df
          .format(bidAggregate.firstEntry()
              .getValue()) + "\n");
      str.append("\n");
    }
    str.append("bids:\n");

    bidAggregate.entrySet().forEach(entry -> {
      str.append(df.format(entry.getKey()) + "  |  " + df.format(entry.getValue()));
      if (bidKraken.containsKey(entry.getKey()) && bidBitfinex.containsKey(entry.getKey())) {
        str.append("  Aggregate\n");
      } else if (bidKraken.containsKey(entry.getKey())) {
        str.append("  Kraken\n");
      } else {
        str.append("  Bitfinex\n");
      }

    });
    str.append("=================================\n");
    str.append("Total asks: " + askAggregate.size() + "\n");
    str.append("Total bids: " + bidAggregate.size() + "\n");
    str.append("=================================\n");

    System.out.println(str.toString());
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
