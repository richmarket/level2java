# level2 行情api

## demo

"""
SourceStockMDService mdService =
                new SourceStockMDService("user", "userpass", "",
                        new MDListener() {
                            @Override
                            public void handleMarketData(MarketData marketData) {

                                if (marketData.getSymbol().equals("002131.SZ")) {
                                    System.out.println(marketData);
                                }
                            }

                            @Override
                            public void handleIndexData(IndexData indexData) {
                                if (indexData.getSymbol().equals("000001.SH")) {
                                    System.out.println(indexData);
                                }
                            }

                            @Override
                            public void handleTradeOrderQueue(TradeOrderQueue tradeOrderQueue) {

                                if (tradeOrderQueue.getSymbol().equals("002131.SZ")) {
                                    System.out.println(tradeOrderQueue);
                                }
                            }

                            @Override
                            public void handleTradeOrder(TradeOrder tradeOrder) {
                                if (tradeOrder.getSymbol().equals("002131.SZ")) {
                                    System.out.println(tradeOrder);
                                }

                            }

                            @Override
                            public void handleTradeTransaction(TradeTransaction tradeTransaction) {

                                if (tradeTransaction.getSymbol().equals("002131.SZ")) {
                                    System.out.println(tradeTransaction);
                                }
                            }
                        });
        mdService
                .subscribeOrder()
                .subscribeTran()
                .subscribeQueue()
                .subscribeMarketData();
                .subscribeIndexData();
"""