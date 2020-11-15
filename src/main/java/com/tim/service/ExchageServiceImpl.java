package com.tim.service;

import com.tim.websocket.Bitfinex;
import com.tim.websocket.Kraken;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Service
@Data
public class ExchageServiceImpl implements ExchageService {

  private final String KRAKEN_STREAM_URL = "wss://ws.kraken.com";
  private final String MESSAGE_KRAKEN = "{ \"event\": \"subscribe\",  \"pair\": [\"XBT/USD\"],  \"subscription\": {\"name\": \"book\"} }";

  public final String BITFINEX_STREAM_URL = "wss://api-pub.bitfinex.com/ws/2";
  public final String MESSAGE_BITFINEX = "  { \"event\": \"subscribe\", \"channel\": \"book\", \"symbol\": \"tBTCUSD\"}";


  OrderBookService orderBookService;

  @Autowired
  public ExchageServiceImpl(OrderBookService orderBookService) {
    this.orderBookService = orderBookService;
  }



  @Override
  public void initiateWebSocketConnection() {
    WebSocketClient client = new StandardWebSocketClient();
    client.doHandshake(new Kraken(orderBookService), KRAKEN_STREAM_URL);
    client.doHandshake(new Bitfinex(orderBookService), BITFINEX_STREAM_URL);
  }
}
