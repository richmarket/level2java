package com.wuma.md.source.stockmd;

import com.wuma.md.source.stockmd.bean.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.NoRouteToHostException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SourceStockMDService {
    // 账号
    private String account;
    // 密码
    private String password;
    // ip地址
    private String host;

    private MDListener mdListener;

    private int indexDataPort = 32920;
    private int marketDataPort = 33920;
    private int queuePort = 36920;
    private int tranPort = 34920;
    private int orderPort = 35920;

    private ChannelFuture indexDataChannelFuture;
    private ChannelFuture marketDataChannelFuture;
    private ChannelFuture queueChannelFuture;
    private ChannelFuture tranChannelFuture;
    private ChannelFuture orderChannelFuture;

    private EventLoopGroup group;
    /**
    private EventLoopGroup marketDataEventLoop;
    private EventLoopGroup queueEventLoop;
    private EventLoopGroup tranEventLoop;
    private EventLoopGroup orderEventLoop;
     **/


    /**
     * 构造
     * @param account 账号
     * @param password 密码
     * @param host 地址
     * @param mdListener 行情处理器
     */
    public SourceStockMDService(String account, String password, String host,MDListener mdListener) {
        this.account = account;
        this.password = password;
        this.host = host;
        this.mdListener = mdListener;
        this.group = new NioEventLoopGroup();
    }

    private ChannelFuture genChannelFuture(int port) throws InterruptedException {

        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class)  // 使用NioSocketChannel来作为连接用的channel类
                .handler(new ChannelInitializer<SocketChannel>() { // 绑定连接初始化器
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {

                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new RpcEncoder()); //编码request
                        pipeline.addLast(new RpcDecoder()); //解码response
                        pipeline.addLast(new ClientMsgHandler(SourceStockMDService.this,port));

                    }
                });
        //发起异步连接请求，绑定连接端口和host信息
        final ChannelFuture future = b.connect(host, port).sync();

        future.addListener((ChannelFutureListener) arg0 -> {
            if (future.isSuccess()) {
                log.info("连接 {} : {} 成功",host,port);
                sendSubInfo(port);

            } else {
                log.error("连接 {} : {} 失败,30s后重试",host,port);
                EventLoop loop = future.channel().eventLoop();
                loop.schedule(()->{
                    this.reconnect(port);
                },30, TimeUnit.SECONDS);
            }
        });

        return future;
    }

    private void sendSubInfo(int port) {
        try {
            if (port == indexDataPort) {
                String secu = "level2_api_33617" + "\n" + "login_user" + "\n" + account + "\n" + password + "\n" + "SH_2;SZ_2" + "\n" + "Index0" + "\n" + "0";
                this.indexDataChannelFuture.channel().writeAndFlush(secu);
            } else if (port == marketDataPort ) {
                String secu = "level2_api_33617" + "\n" + "login_user" + "\n" + account + "\n" + password + "\n" + "SH_2;SZ_2" + "\n" + "Market0" + "\n" + "0";
                this.marketDataChannelFuture.channel().writeAndFlush(secu);
            } else if (port == queuePort) {
                String secu = "level2_api_33617" + "\n" + "login_user" + "\n" + account + "\n" + password + "\n" + "SH_2;SZ_2" + "\n" + "Queue0" + "\n" + "0";
                this.queueChannelFuture.channel().writeAndFlush(secu);
            } else if (port == tranPort) {
                String secu = "level2_api_33617" + "\n" + "login_user" + "\n" + account + "\n" + password + "\n" + "SH_2;SZ_2" + "\n" + "Tran0" + "\n" + "0";
                this.tranChannelFuture.channel().writeAndFlush(secu);
            } else if (port == orderPort) {
                String secu = "level2_api_33617" + "\n" + "login_user" + "\n" + account + "\n" + password + "\n" + "SH_2;SZ_2" + "\n" + "Order0" + "\n" + "0";
                this.orderChannelFuture.channel().writeAndFlush(secu);
            }
        }catch (Exception e) {
            log.error("reconnect error" ,e);
        }
    }


    public void reconnect(int port) {
        try {
            if (port == indexDataPort) {
                this.subscribeIndexData();
            } else if (port == marketDataPort ) {
                this.subscribeMarketData();
            } else if (port == queuePort) {
                this.subscribeQueue();
            } else if (port == tranPort) {
                this.subscribeTran();
            } else if (port == orderPort) {
                this.subscribeOrder();
            }
        }catch (Exception e) {
            log.error("reconnect error" ,e);
            try {
                Thread.sleep(15*1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            reconnect(port);

        }
    }

    public SourceStockMDService subscribeIndexData(int port) throws InterruptedException {
        this.indexDataPort = port;
        return this.subscribeIndexData();
    }

    public SourceStockMDService subscribeIndexData() throws InterruptedException {
        this.indexDataChannelFuture = this.genChannelFuture(this.indexDataPort);
        return this;
    }

    public SourceStockMDService subscribeMarketData(int port) throws InterruptedException {
        this.marketDataPort = port;
        return this.subscribeMarketData();
    }

    public SourceStockMDService subscribeMarketData() throws InterruptedException {
        this.marketDataChannelFuture = this.genChannelFuture(this.marketDataPort);
        return this;
    }

    public SourceStockMDService subscribeQueue(int port) throws InterruptedException {
        this.queuePort = port;
        return this.subscribeQueue();
    }

    public SourceStockMDService subscribeQueue() throws InterruptedException {
        this.queueChannelFuture = this.genChannelFuture(this.queuePort);
        return this;
    }

    public SourceStockMDService subscribeTran(int port) throws InterruptedException {
        this.tranPort = port;
        return this.subscribeTran();
    }

    public SourceStockMDService subscribeTran() throws InterruptedException {
        this.tranChannelFuture = this.genChannelFuture(this.tranPort);
        return this;
    }
    public SourceStockMDService subscribeOrder(int port) throws InterruptedException {
        this.orderPort = port;
        return this.subscribeOrder();
    }

    public SourceStockMDService subscribeOrder() throws InterruptedException {
        this.orderChannelFuture = this.genChannelFuture(this.orderPort);
        return this;
    }

    public MDListener getMdListener() {
        return this.mdListener;
    }

    public static void main(String[] args) throws InterruptedException {
        SourceStockMDService mdService =
                new SourceStockMDService("账号", "密码", "ip地址",
                        new MDListener() {
                            @Override
                            public void handleMarketData(MarketData marketData) {

                                if (marketData.getSymbol().equals("002131.SZ")) {
                                    System.out.println(marketData);
                                }
                            }

                            @Override
                            public void handleIndexData(IndexData indexData) {
//                                if (indexData.getSymbol().equals("000001.SH")) {
//                                    System.out.println(indexData);
//                                }
                            }

                            @Override
                            public void handleTradeOrderQueue(TradeOrderQueue tradeOrderQueue) {

//                                if (tradeOrderQueue.getSymbol().equals("002131.SZ")) {
//                                    System.out.println(tradeOrderQueue);
//                                }
                            }

                            @Override
                            public void handleTradeOrder(TradeOrder tradeOrder) {
//                                if (tradeOrder.getSymbol().equals("002131.SZ")) {
//                                    System.out.println(tradeOrder);
//                                }

                            }

                            @Override
                            public void handleTradeTransaction(TradeTransaction tradeTransaction) {

//                                if (tradeTransaction.getSymbol().equals("002131.SZ")) {
//                                    System.out.println(tradeTransaction);
//                                }
                            }
                        });
        mdService
                .subscribeOrder()
                .subscribeTran()
                .subscribeQueue()
                .subscribeMarketData();
//                .subscribeIndexData();
    }

}
