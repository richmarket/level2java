package com.wuma.md.source.stockmd.bean;

import lombok.Data;

/**
 * 委托队列
 */
@Data
public class TradeOrderQueue {
    private String symbol;
   private String code;
    private String exchange;
   private int date;
   private int time;
   private int askPrice;
   private int bidPrice;
   private int[] bidVolumes;
   private int[] askVolumes;
}
