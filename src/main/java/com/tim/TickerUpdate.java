package com.tim;

import java.util.Arrays;

public class TickerUpdate {
  String a[][];
  String b[][];

  public TickerUpdate() {
  }

  public TickerUpdate(String[][] a, String[][] b) {
    this.a = a;
    this.b = b;
  }

  public String[][] getA() {
    return a;
  }

  public void setA(String[][] a) {
    this.a = a;
  }

  public String[][] getB() {
    return b;
  }

  public void setB(String[][] b) {
    this.b = b;
  }

  @Override
  public String toString() {
    return "TickerUpdate{" +
        "a=" + Arrays.toString(a) +
        ", b=" + Arrays.toString(b) +
        '}';
  }
}
