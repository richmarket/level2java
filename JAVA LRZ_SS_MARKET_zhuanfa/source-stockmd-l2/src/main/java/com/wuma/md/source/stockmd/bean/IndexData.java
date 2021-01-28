package com.wuma.md.source.stockmd.bean;

import lombok.Data;

/**
 * 指数行情
 */
@Data
public class IndexData {
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
    // 成交量
    private long volume;
    // 成交额
    private long turnover;

}
