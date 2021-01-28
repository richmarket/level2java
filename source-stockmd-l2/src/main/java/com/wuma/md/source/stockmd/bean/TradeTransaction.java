package com.wuma.md.source.stockmd.bean;

import lombok.Data;

/**
 * 逐笔成交
 */
@Data
public class TradeTransaction {

    private String symbol;
    private String code;
    private String exchange;
    private int date;
    private int time;
    private String tradeNo;
    private int matchedPrice;
    private long matchedAmount;
    private long matchedBalance;
    private char side;
    private char type;
    private String askOrderNo;
    private String bidOrderNo;
}
