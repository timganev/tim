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
            bidAggregate.put(price, amount);
          } else {
            askBitfinex.put(price, amount * -1);
            askAggregate.put(price, amount * -1);
          }

        } else {
          askBitfinex.remove(price);
          bidBitfinex.remove(price);

          askAggregate.remove(price);
          if (askKraken.containsKey(price)) {
            askAggregate.put(price, askKraken.get(price));
          }

          bidAggregate.remove(price);
          if (bidKraken.containsKey(price)) {
            bidAggregate.put(price, bidKraken.get(price));
          }
        }

        printBook();

      }
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
