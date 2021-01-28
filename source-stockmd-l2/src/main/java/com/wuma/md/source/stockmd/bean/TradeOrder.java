package com.wuma.md.source.stockmd.bean;

import lombok.Data;

/**
 * 逐笔委托
 */
@Data
public class TradeOrder {

    private String symbol;
    private String code;
    private String exchange;
    private int date;
    private int time;
    private String orderNo;
    private int price;
    private int amount;
    private char type;
    private char side;
}
