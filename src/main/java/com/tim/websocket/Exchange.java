package com.tim.websocket;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import org.springframework.web.socket.WebSocketHandler;

public interface Exchange extends WebSocketHandler {

  TreeMap<Double, Double> askAgreggate = new TreeMap<>(Comparator.reverseOrder());
  TreeMap<Double, Double> bidAgreggate = new TreeMap<>(Comparator.reverseOrder());

  HashMap<Double, Double> askKraken = new HashMap<>();
  HashMap<Double, Double> bidKraken = new HashMap<>();

  HashMap<Double, Double> askBitfinex = new HashMap<>();
  HashMap<Double, Double> bidBitfinex = new HashMap<>();

  DecimalFormat df = new DecimalFormat("0.0000000000");

}
