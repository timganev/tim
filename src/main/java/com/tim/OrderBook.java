package com.tim;

import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class OrderBook {
  ConcurrentSkipListMap askBook = new ConcurrentSkipListMap<Float, Integer>(new TreeMap());
  ConcurrentSkipListMap bidBook = new ConcurrentSkipListMap<Float, Integer>(new TreeMap());

  public void update(TickerUpdate update) {
    if (update.getA() != null) {
      for (String[] ticker : update.getA()) {
        askBook.put(Float.valueOf(ticker[0]), Integer.valueOf(ticker[0]));
      }
    }
  }
}
