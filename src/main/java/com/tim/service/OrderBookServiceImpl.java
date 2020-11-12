package com.tim.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tim.websocket.Bitfinex;
import com.tim.websocket.Kraken;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Data
@Service
public class OrderBookServiceImpl implements OrderBookService {

  public static ConcurrentSkipListMap<Double, Double> askAggregate = new ConcurrentSkipListMap<>(
      Comparator.reverseOrder());
  public static ConcurrentSkipListMap<Double, Double> bidAggregate = new ConcurrentSkipListMap<>(
      Comparator.reverseOrder());
//  TreeMap<Double, Double> askAggregate = new TreeMap<>(Comparator.reverseOrder());
//  TreeMap<Double, Double> bidAggregate = new TreeMap<>(Comparator.reverseOrder());

  public static HashMap<Double, Double> askKraken = new HashMap<>();
  public static HashMap<Double, Double> bidKraken = new HashMap<>();

  public static HashMap<Double, Double> askBitfinex = new HashMap<>();
  public static HashMap<Double, Double> bidBitfinex = new HashMap<>();

  DecimalFormat df = new DecimalFormat("0.0000000000");

  @Autowired
  Bitfinex bitfinex;

  @Autowired
  Kraken kraken;

  @Override
  public void updateBitfinex(JsonNode root) {

    if (root.isArray() && root.size() > 1 && root.get(1).isArray()) {
      JsonNode node = root.get(1);

      Double price = node.get(0).asDouble();
      Long count = node.get(1).asLong();
      Double amount = node.get(2).asDouble();

      if (count != null && price != null && amount != null) {

        if (count > 0) {

          //BID
          if (amount > 0) {
            bidBitfinex.put(price, amount);

            //BID Aggregate
            if (bidKraken.containsKey(price)) {
              bidAggregate.put(price, (bidBitfinex.get(price) + bidKraken.get(price)));
            } else {
              bidAggregate.put(price, amount);
            }

            //ASK
          } else {
            // reverse negative sign
            askBitfinex.put(price, amount * -1);

            //ASK Aggregate
            if (askKraken.containsKey(price)) {
              askAggregate.put(price, (askBitfinex.get(price) + askKraken.get(price)));
            } else {
              askAggregate.put(price, askBitfinex.get(price));
            }
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

  @Override
  public void updateKraken(JsonNode root) {

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

            //BID Aggregate
            if (askBitfinex.containsKey(price)) {
              askAggregate.put(price, (askBitfinex.get(price) + askKraken.get(price)));
            } else {
              askAggregate.put(price, amount);
            }


          } else {
            askKraken.remove(price);

            askAggregate.remove(price);
            if (askBitfinex.containsKey(price)) {
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

            //ASK Aggregate
            if (bidBitfinex.containsKey(price)) {
              bidAggregate.put(price, (bidBitfinex.get(price) + bidKraken.get(price)));
            } else {
              bidAggregate.put(price, amount);
            }

          } else {
            bidKraken.remove(price);

            bidAggregate.remove(price);
            if (bidBitfinex.containsKey(price)) {
              bidAggregate.put(price, bidBitfinex.get(price));
            }
          }
        });
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

}
