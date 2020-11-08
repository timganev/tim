//package com.tim;
//
//import org.springframework.messaging.Message;
//import org.springframework.messaging.simp.stomp.StompHeaders;
//import org.springframework.messaging.simp.stomp.StompSession;
//import org.springframework.messaging.simp.stomp.StompSessionHandler;
//import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
//
//public class MyStompSessionHandler extends StompSessionHandlerAdapter {
//  @Override
//  public void afterConnected(
//      StompSession session, StompHeaders connectedHeaders) {
//    session.subscribe("/topic/messages", this);
//    System.out.println("Sending ");
//
//    session.send(new StompHeaders(), "{\n"
//          + "  \"event\": \"subscribe\",\n"
//          + "  \"pair\": [\n"
//          + "    \"XBT/USD\",\n"
//          + "    \"XBT/EUR\"\n"
//          + "  ],\n"
//          + "  \"subscription\": {\n"
//          + "    \"name\": \"ticker\"\n"
//          + "  }\n"
//          + "}");
//  }
//  @Override
//  public void handleFrame(StompHeaders headers, Object payload) {
//    Message msg = (Message) payload;
//    System.out.println("Received : " + msg.toString());
//  }
//}
