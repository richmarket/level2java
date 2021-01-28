package com.wuma.md.source.stockmd;

import com.wuma.md.source.stockmd.bean.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.channel.group.ChannelGroup;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**

 */
@Slf4j
public class ClientMsgHandler extends ChannelInboundHandlerAdapter {

    private SourceStockMDService stockMDService;
    private int port;
    private long lastHeart =0;

    public ClientMsgHandler(SourceStockMDService stockMDService, int port) {
        this.stockMDService = stockMDService;
        this.port = port;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("port: {} 已激活",port);
        lastHeart = System.currentTimeMillis();
        // 检测心跳
        new Thread(()->{
            while (true) {
               long now = System.currentTimeMillis();
                if (now - lastHeart > 20 * 1000) {
                    log.warn("port : {} 心跳丢失",port);
                    ctx.close();
                    stockMDService.reconnect(port);
                    break;
                }
                try {
                    Thread.sleep(15*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void channelRead(ChannelHandlerContext ctx, Object obj) {

        if (stockMDService.getMdListener() == null) {
            return;
        }
        if (obj instanceof IndexData) {
            stockMDService.getMdListener().handleIndexData((IndexData) obj);
        } else if (obj instanceof MarketData){
            stockMDService.getMdListener().handleMarketData((MarketData) obj);
        } else if (obj instanceof TradeOrder) {
           stockMDService.getMdListener().handleTradeOrder((TradeOrder) obj);
        } else if (obj instanceof TradeOrderQueue) {
            stockMDService.getMdListener().handleTradeOrderQueue((TradeOrderQueue) obj);
        } else if (obj instanceof TradeTransaction) {
            stockMDService.getMdListener().handleTradeTransaction((TradeTransaction) obj);
        } else if (obj instanceof Heart){
            log.info("port :{} 更新心跳",port);
            this.lastHeart = System.currentTimeMillis();
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("",cause);
        ctx.close();
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.error("channel inactive");
        ctx.close();
    }



}
