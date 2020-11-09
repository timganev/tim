package com.tim;

import com.tim.websocket.Bitfinex;
import com.tim.websocket.Kraken;
import java.util.Scanner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

//import com.tim.websocket.Kraken;

@SpringBootApplication
public class TimApplication implements CommandLineRunner {

  String KRAKEN_STREAM_URL = "wss://ws.kraken.com";
  String BITFINEX_STREAM_URL = "wss://api-pub.bitfinex.com/ws/2";

  public static void main(String[] args) {
    SpringApplication.run(TimApplication.class, args);
  }


  @Override
  public void run(String... args) throws Exception {
    WebSocketClient client = new StandardWebSocketClient();
    client.doHandshake(new Kraken(), KRAKEN_STREAM_URL);
    client.doHandshake(new Bitfinex(), BITFINEX_STREAM_URL);

    new Scanner(System.in).nextLine(); // Don't close immediately.
  }
}
