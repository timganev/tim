package com.tim.websocket;

import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import org.springframework.web.socket.WebSocketHandler;

public interface ExchangeWebSocketHandler extends WebSocketHandler {

  public static TreeMap<Double, Double> classAskMap = new TreeMap<>(Comparator.reverseOrder());
  public static TreeMap<Double, Double> classBidMap = new TreeMap<>(Comparator.reverseOrder());

//  TreeMap<Integer, String> map = new TreeMap<>(Comparator.reverseOrder());

}
