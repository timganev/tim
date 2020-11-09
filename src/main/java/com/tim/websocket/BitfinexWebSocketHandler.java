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
public class BitfinexWebSocketHandler implements ExchangeWebSocketHandler {


  HashMap<Long, Double> ordersMap = new HashMap<>();

  HashMap<Double, HashMap<Long, Double>> askMap = new HashMap<>();
  HashMap<Double, HashMap<Long, Double>> bidMap = new HashMap<>();


  public static final String MESSAGE =
      "  { \"event\": \"subscribe\", \"channel\": \"book\", \"prec\": \"R0\", \"freq\":\"F1\", \"symbol\": \"tBTCUSD\" }";

  private static DecimalFormat df = new DecimalFormat("0.0000000000");
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
      if (node.isArray() && node.size() == 3) {

        Long orderId = node.get(0).asLong();
        Double price = node.get(1).asDouble();
        Double amount = node.get(2).asDouble();

        if (price == 0) {
          removeOrder(orderId);
        } else {
          ordersMap.put(orderId, price);

          if (askMap.containsKey(price)) {
            askMap.get(price).put(orderId, amount);

            if (amount < 0) {
              classAskMap.put(price, amount * -1);
            } else {
              classBidMap.put(price, amount);
            }

          } else {
            HashMap<Long, Double> innerMap = new HashMap<>();
            innerMap.put(orderId, amount);
            askMap.put(price, innerMap);

            Double sum = 0.0;
            for (Double f : innerMap.values()) {
              sum += f;
            }

            if (sum < 0) {
              classAskMap.put(price, sum * -1);
            } else {
              classBidMap.put(price, sum);
            }
          }

        }
        printBook();
      }

    }
  }

  private void removeOrder(Long orderId) {
    Double recordPrice = ordersMap.get(orderId);
    // ASK
    if (askMap.containsKey(recordPrice)) {
      Double subAmount = askMap.get(recordPrice).get(orderId);

      if (classAskMap.containsKey(recordPrice)
          && classAskMap.get(recordPrice) - subAmount > 0) {
        classAskMap.put(recordPrice, classAskMap.get(recordPrice) - subAmount);
      } else {
        classAskMap.remove(recordPrice);
      }
      askMap.get(recordPrice).remove(orderId);
    }
    // BID
    if (bidMap.containsKey(recordPrice)) {
      Double subAmount = bidMap.get(recordPrice).get(orderId);

      if (classBidMap.containsKey(recordPrice)
          && classBidMap.get(recordPrice) - subAmount > 0) {
        classBidMap.put(recordPrice, classBidMap.get(recordPrice) - subAmount);
      } else {
        classBidMap.remove(recordPrice);
      }
      bidMap.get(recordPrice).remove(orderId);
    }
    ordersMap.remove(orderId);
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
              .format(classBidMap.lastEntry()
                  .getValue()));
    }
    System.out.println();
    System.out.println("bids:");
    classBidMap.entrySet().forEach(entry -> {
      System.out.println(df.format(entry.getKey()) + "  |  " + df.format(entry.getValue()));
    });
    System.out.println("##################################");
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
