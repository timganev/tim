package com.tim.websocket;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import org.springframework.web.socket.WebSocketHandler;

public interface Exchange extends WebSocketHandler {

  public static TreeMap<Double, Double> classAskMap = new TreeMap<>(Comparator.reverseOrder());
  public static TreeMap<Double, Double> classBidMap = new TreeMap<>(Comparator.reverseOrder());
  DecimalFormat df = new DecimalFormat("0.0000000000");

}
