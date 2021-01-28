package com.wuma.md.source.stockmd.bean;

import lombok.Data;

/**
 * 股票行情
 */
@Data
public class MarketData {
   private String symbol;
   private String code;
   private String exchange;
   private int date;
   private int time;
   private String status;
   private int preClose;
   private int open;
   private int high;
   private int low;
   private int now;
    // 涨停价
    private int upLimit;
    // 跌停价
    private int downLimit;
    // 成交量
    private long volume;
    // 成交额
    private long turnover;

    // 买五档
    private Bid[] bids;
    // 卖五档
    private Ask[] asks;
}
