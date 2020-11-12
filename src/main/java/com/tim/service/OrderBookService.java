package com.tim.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface OrderBookService {

  void updateBitfinex(JsonNode root);

  void updateKraken(JsonNode root);
}
