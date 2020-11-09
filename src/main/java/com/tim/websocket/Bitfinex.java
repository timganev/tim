package com.tim.websocket;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.DecimalFormat;
import java.util.HashMap;
import lombok.Data;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Data
public class Bitfinex implements Exchange {


  HashMap<Long, Double> ordersMap = new HashMap<>();
  HashMap<Double, HashMap<Long, Double>> askMap = new HashMap<>();
  HashMap<Double, HashMap<Long, Double>> bidMap = new HashMap<>();

  public static final String MESSAGE = "  { \"event\": \"subscribe\", \"channel\": \"book\", \"prec\": \"R0\", \"freq\":\"F0\", \"symbol\": \"tBTCUSD\" }";

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

      Long orderId = node.get(0).asLong();
      Double price = node.get(1).asDouble();
      Double amount = node.get(2).asDouble();

      if (orderId != null && price != null && amount != null) {

        //ASK
        if (askMap.containsKey(price) && amount < 0 && price > 0) {
          amount = amount * -1;
          askMap.get(price).put(orderId, amount);
          HashMap<Long, Double> innerMap = askMap.get(price);
          Double sum = 0.0;
          for (Double amm : innerMap.values()) {
            sum += amm;
          }
          classAskMap.put(price, sum);
        } else if (amount < 0 && price > 0) {
          amount = amount * -1;
          HashMap<Long, Double> innerMap = new HashMap<>();
          innerMap.put(orderId, amount);
          askMap.put(price, innerMap);
          classAskMap.put(price, amount);
        }

        //BID
        if (bidMap.containsKey(price) && amount > 0 && price > 0) {

          bidMap.get(price).put(orderId, amount);
          HashMap<Long, Double> innerMap = bidMap.get(price);
          Double sum = 0.0;
          for (Double amm : innerMap.values()) {
            sum += amm;
          }
          classBidMap.put(price, sum);
        } else if (amount > 0 && price > 0) {

          HashMap<Long, Double> innerMap = new HashMap<>();
          innerMap.put(orderId, amount);
          bidMap.put(price, innerMap);
          classBidMap.put(price, amount);
        }

        ordersMap.put(orderId, price);

        if (price == 0) {
          removeOrder(orderId);
        }

        printBook();


      }
    }

  }

  private void removeOrder(Long orderId) {
    Double recordPrice = ordersMap.get(orderId);
    ordersMap.remove(orderId);

    // ASK
    if (askMap.containsKey(recordPrice)) {
      Double subAmount = askMap.get(recordPrice).get(orderId);
      askMap.get(recordPrice).remove(orderId);
      if (askMap.get(recordPrice).size() == 0) {
        askMap.remove(recordPrice);
      }

      if (classAskMap.containsKey(recordPrice)
          && classAskMap.get(recordPrice) - subAmount >= 0) {
        classAskMap.put(recordPrice, classAskMap.get(recordPrice) - subAmount);
      } else {
        classAskMap.remove(recordPrice);
      }


    }
    // BID
    if (bidMap.containsKey(recordPrice)) {
      Double subAmount = bidMap.get(recordPrice).get(orderId);
      bidMap.get(recordPrice).remove(orderId);
      if (bidMap.get(recordPrice).size() == 0) {
        bidMap.remove(recordPrice);
      }

      if (classBidMap.containsKey(recordPrice)
          && classBidMap.get(recordPrice) - subAmount >= 0) {
        classBidMap.put(recordPrice, classBidMap.get(recordPrice) - subAmount);
      } else {
        classBidMap.remove(recordPrice);
      }

    }

    System.out.println("Remove Bitfinex order : " + orderId);
  }

  private void printBook() {
    System.out.println();
    System.out.println("Order book");
    System.out.println("asks:");
    classAskMap.entrySet().forEach(entry -> {
      System.out.println(df.format(entry.getKey()) + "  |  " + df.format(entry.getValue()));
    });
    if (classAskMap.size() > 0) {
      System.out.println();
      System.out.println(
          "best ask: " + df.format(classAskMap.lastEntry().getKey()) + "  |  " + df
              .format(classAskMap.lastEntry()
                  .getValue()));
    }

    if (classBidMap.size() > 0) {
      System.out.println(
          "best bid: " + df.format(classBidMap.firstEntry().getKey()) + "  |  " + df
              .format(classBidMap.firstEntry()
                  .getValue()));
    }
    System.out.println();
    System.out.println("bids:");
    classBidMap.entrySet().forEach(entry -> {
      System.out.println(df.format(entry.getKey()) + "  |  " + df.format(entry.getValue()));
    });
    System.out.println("=================================");
    System.out.println("Aggregate asks: " + classAskMap.size());
    System.out.println("Bitfinex  asks: " + askMap.size());
    System.out.println("Aggregate bids: " + classBidMap.size());
    System.out.println("Bitfinex  bids: " + bidMap.size());
    System.out.println("ordersMap  size: " + ordersMap.size());
    System.out.println("=================================");
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
