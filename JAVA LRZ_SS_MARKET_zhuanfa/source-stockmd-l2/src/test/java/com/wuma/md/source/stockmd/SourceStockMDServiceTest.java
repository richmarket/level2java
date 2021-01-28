package com.wuma.md.source.stockmd;

import com.wuma.md.source.stockmd.bean.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SourceStockMDServiceTest {

    SourceStockMDService sourceStockMDService =null;

    @Test
    public void setUp() throws Exception {
        sourceStockMDService = new SourceStockMDService("’À∫≈", "√‹¬Î", "ipµÿ÷∑", new MDListener() {
            @Override
            public void handleMarketData(MarketData marketData) {
                System.out.println(marketData);
            }

            @Override
            public void handleIndexData(IndexData indexData) {

            }

            @Override
            public void handleTradeOrderQueue(TradeOrderQueue tradeOrderQueue) {

            }

            @Override
            public void handleTradeOrder(TradeOrder tradeOrder) {

            }

            @Override
            public void handleTradeTransaction(TradeTransaction tradeTransaction) {

            }
        });
    }

    @Test
    public void test() {
        System.out.println(1);
    }
}