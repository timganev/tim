//package com.tim.old;
//
//import java.text.DecimalFormat;
//import java.util.Comparator;
//import java.util.TreeMap;
//import lombok.Data;
//
//
//@org.springframework.stereotype.Service
//public class OrderBook {
//  //  ConcurrentSkipListMap askBook = new ConcurrentSkipListMap<Float, Integer>(new TreeMap());
////  ConcurrentSkipListMap bidBook = new ConcurrentSkipListMap<Float, Integer>(new TreeMap());
//  public static DecimalFormat df = new DecimalFormat("0.0000000000");
//  private static TreeMap<Double, Double> askAgreggate = new TreeMap<>(Comparator.reverseOrder());
//  private static TreeMap<Double, Double> bidAgreggate = new TreeMap<>(Comparator.reverseOrder());
//
//  public OrderBook() {
//  }
//
//  public static TreeMap<Double, Double> getAskAgreggate() {
//    return askAgreggate;
//  }
//
//  public static void setAskAgreggate(TreeMap<Double, Double> askAgreggate) {
//    OrderBook.askAgreggate = askAgreggate;
//  }
//
//  public static TreeMap<Double, Double> getBidAgreggate() {
//    return bidAgreggate;
//  }
//
//  public static void setBidAgreggate(TreeMap<Double, Double> bidAgreggate) {
//    OrderBook.bidAgreggate = bidAgreggate;
//  }
//
//
//
//
//  public void printBook(TreeMap<Double, Double> askAgreggate, TreeMap<Double, Double> bidAgreggate) {
//    System.out.println();
//    System.out.println("Order book");
//    System.out.println("asks:");
//    askAgreggate.entrySet().forEach(entry -> {
//      System.out.println(df.format(entry.getKey()) + "  |  " + df.format(entry.getValue()));
//    });
//    if (askAgreggate.size() > 0) {
//      System.out.println();
//      System.out.println(
//          "best ask: " + df.format(askAgreggate.lastEntry().getKey()) + "  |  " + df
//              .format(askAgreggate.lastEntry()
//                  .getValue()));
//    }
//
//    if (bidAgreggate.size() > 0) {
//      System.out.println(
//          "best bid: " + df.format(bidAgreggate.firstEntry().getKey()) + "  |  " + df
//              .format(bidAgreggate.firstEntry()
//                  .getValue()));
//    }
//    System.out.println();
//    System.out.println("bids:");
//    bidAgreggate.entrySet().forEach(entry -> {
//      System.out.println(df.format(entry.getKey()) + "  |  " + df.format(entry.getValue()));
//    });
//    System.out.println("=================================");
//    System.out.println("Aggregate asks: " + askAgreggate.size());
//    System.out.println("Aggregate bids: " + bidAgreggate.size());
//    System.out.println("=================================");
//  }
//
//
//
//}
