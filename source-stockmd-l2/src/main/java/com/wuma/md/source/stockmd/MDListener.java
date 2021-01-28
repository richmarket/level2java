package com.wuma.md.source.stockmd;

import com.wuma.md.source.stockmd.bean.*;

/**
 * 行情订阅处理器
 */
public interface MDListener {
    void handleMarketData(MarketData marketData);
    void handleIndexData(IndexData indexData);
    void handleTradeOrderQueue(TradeOrderQueue tradeOrderQueue);
    void handleTradeOrder(TradeOrder tradeOrder);
    void handleTradeTransaction(TradeTransaction tradeTransaction);
}
