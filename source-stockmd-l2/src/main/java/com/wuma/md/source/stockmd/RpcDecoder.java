package com.wuma.md.source.stockmd;

import com.wuma.md.source.stockmd.bean.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class RpcDecoder extends ByteToMessageDecoder {

    private final static byte[] HEART = new byte[]{'H','e','a','r','t','_','b','e','a','r','t'};
    private final static byte[] BEGIN = new byte[]{'S','S','_','b','e','g','i','n'};

    private final static byte[] COMPRESS_MARKET = new byte[]{'@','m','a','r','k','e','t','@'};
    private final static byte[] UN_COMPRESS_MARKET = new byte[]{'@','M','a','r','k','e','t','@'};

    private final static byte[] COMPRESS_INDEX = new byte[]{'@','i','n','d','e','x','_','@'};
    private final static byte[] UN_COMPRESS_INDEX = new byte[]{'@','I','n','d','e','x','_','@'};

    private final static byte[] COMPRESS_QUEUE = new byte[]{'@','q','u','e','u','e','_','@'};
    private final static byte[] UN_COMPRESS_QUEUE = new byte[]{'@','Q','u','e','u','e','_','@'};

    private final static byte[] COMPRESS_TRAN = new byte[]{'@','t','r','a','n','_','_','@'};
    private final static byte[] UN_COMPRESS_TRAN = new byte[]{'@','T','r','a','n','_','_','@'};

    private final static byte[] COMPRESS_ORDER = new byte[]{'@','o','r','d','e','r','_','@'};
    private final static byte[] UN_COMPRESS_ORDER = new byte[]{'@','O','r','d','e','r','_','@'};

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in.readableBytes() >= 11) {
            in.markReaderIndex();
            byte[] heart = new byte[11];
            in.readBytes(heart);

            if (!Arrays.equals(heart,HEART)) {
                in.resetReaderIndex();
                if (in.readableBytes() >= 8+8+13) {
                   byte[] top = new byte[8];
                   in.readBytes(top);
                   if (Arrays.equals(top,BEGIN)) {
                      byte[] typeMarket = new byte[8];
                      in.readBytes(typeMarket);
                      // 取下面13位
                       byte[] marketLength = new byte[13];
                       in.readBytes(marketLength);
                       String sMarketLength = new String(marketLength);
                       int iMarketLength = NumberUtils.toInt(sMarketLength);
                       if (in.readableBytes() < iMarketLength) {
                           in.resetReaderIndex();
                           return;
                       } else {
                           byte[] data = new byte[iMarketLength];
                           in.readBytes(data);
                           if (Arrays.equals(typeMarket,COMPRESS_INDEX)) {
                               byte[] uncompressData = uncompress(data);
                               String sData = new String(uncompressData);
                               String[] array = sData.split("#");
                               for (String a : array) {
                                   out.add(this.convertIndexData(a));
                               }
                           } else if (Arrays.equals(typeMarket,COMPRESS_MARKET)) {
                               byte[] uncompressData = uncompress(data);
                               String sData = new String(uncompressData);
                               String[] array = sData.split("#");
                               for (String a : array) {
                                   out.add(this.convertMarketData(a));
                               }
                           } else if (Arrays.equals(typeMarket,COMPRESS_QUEUE)) {
                               byte[] uncompressData = uncompress(data);
                               String sData = new String(uncompressData);
                               String[] array = sData.split("#");
                               for (String a : array) {
                                   out.add(this.convertTradeOrderQueue(a));
                               }
                           } else if (Arrays.equals(typeMarket,COMPRESS_ORDER)) {
                               byte[] uncompressData = uncompress(data);
                               String sData = new String(uncompressData);
                               String[] array = sData.split("#");
                               for (String a : array) {
                                   out.add(this.convertTradeOrder(a));
                               }
                           } else if (Arrays.equals(typeMarket,COMPRESS_TRAN)) {
                               byte[] uncompressData = uncompress(data);
                               String sData = new String(uncompressData);
                               String[] array = sData.split("#");
                               for (String a : array) {
                                   out.add(this.convertTradeTransaction(a));
                               }
                           }

                       }
                   } else {
                       in.resetReaderIndex();
                   }
                }
            } else {
                out.add(new Heart());
            }
        }
    }

    private TradeOrderQueue convertTradeOrderQueue(String data) {
        String[] array = data.split("\\|",-1);
        TradeOrderQueue marketData = new TradeOrderQueue();
        marketData.setSymbol(array[0]);
        marketData.setCode(array[1]);
        marketData.setExchange(array[0].substring(array[0].length()-2));
        marketData.setDate(NumberUtils.toInt(array[2]));
        marketData.setTime(NumberUtils.toInt(array[3]));
        marketData.setAskPrice(NumberUtils.toInt(array[4]));
        marketData.setBidPrice(NumberUtils.toInt(array[6]));
        int askLength = NumberUtils.toInt(array[5]);
        int[] askVolumes = new int[askLength];
        if (array.length < 20) {
            System.out.println(array);
        }
        for (int i = 0; i < askVolumes.length; i++) {
            askVolumes[i] = NumberUtils.toInt(array[8 + i]);
        }
        marketData.setAskVolumes(askVolumes);

        int bidLength = NumberUtils.toInt(array[7]);
        int[] bidVolumes = new int[bidLength];
        for (int i = 0; i < bidVolumes.length; i++) {
            bidVolumes[i] = NumberUtils.toInt(array[58 + i]);
        }
        marketData.setBidVolumes(bidVolumes);

        return marketData;
    }

    private TradeTransaction convertTradeTransaction(String data) {
        String[] array = data.split("\\|",-1);
        TradeTransaction marketData = new TradeTransaction();
        marketData.setSymbol(array[0]);
        marketData.setCode(array[1]);
        marketData.setExchange(array[0].substring(array[0].length()-2));
        marketData.setDate(NumberUtils.toInt(array[2]));
        marketData.setTime(NumberUtils.toInt(array[3]));
        marketData.setTradeNo(array[4]);
        marketData.setMatchedPrice(NumberUtils.toInt(array[5]));
        marketData.setMatchedAmount(NumberUtils.toInt(array[6]));
        marketData.setMatchedBalance(NumberUtils.toLong(array[7]));
        marketData.setSide(array[8].charAt(0));
        marketData.setType(array[9].charAt(0));
        marketData.setAskOrderNo(array[11]);
        marketData.setBidOrderNo(array[12]);

        return marketData;
    }

    private TradeOrder convertTradeOrder(String data) {
        String[] array = data.split("\\|",-1);
        TradeOrder marketData = new TradeOrder();
        marketData.setSymbol(array[0]);
        marketData.setCode(array[1]);
        marketData.setExchange(array[0].substring(array[0].length()-2));
        marketData.setDate(NumberUtils.toInt(array[2]));
        marketData.setTime(NumberUtils.toInt(array[3]));
        marketData.setOrderNo(array[4]);
        marketData.setPrice(NumberUtils.toInt(array[5]));
        marketData.setAmount(NumberUtils.toInt(array[6]));
        marketData.setType(array[7].charAt(0));
        marketData.setSide(array[8].charAt(0));

        return marketData;
    }

    private IndexData convertIndexData(String data) {
        String[] array = data.split("\\|",-1);
        IndexData marketData = new IndexData();
        marketData.setSymbol(array[0]);
        marketData.setCode(array[1]);
        marketData.setExchange(array[0].substring(array[0].length()-2));
        marketData.setDate(NumberUtils.toInt(array[2]));
        marketData.setTime(NumberUtils.toInt(array[3]));
        marketData.setPreClose(NumberUtils.toInt(array[10]));
        marketData.setOpen(NumberUtils.toInt(array[4]));
        marketData.setHigh(NumberUtils.toInt(array[5]));
        marketData.setLow(NumberUtils.toInt(array[6]));
        marketData.setNow(NumberUtils.toInt(array[7]));
        marketData.setVolume(NumberUtils.toLong(array[8]));
        marketData.setTurnover(NumberUtils.toLong(array[9]));

        return marketData;
    }

    private MarketData convertMarketData(String data) {
        String[] array = data.split("\\|",-1);
        MarketData marketData = new MarketData();
        marketData.setSymbol(array[0]);
        marketData.setCode(array[1]);
        marketData.setExchange(array[0].substring(array[0].length()-2));
        marketData.setDate(NumberUtils.toInt(array[2]));
        marketData.setTime(NumberUtils.toInt(array[3]));
        marketData.setStatus(array[4]);
        marketData.setPreClose(NumberUtils.toInt(array[5]));
        marketData.setOpen(NumberUtils.toInt(array[6]));
        marketData.setHigh(NumberUtils.toInt(array[7]));
        marketData.setLow(NumberUtils.toInt(array[8]));
        marketData.setNow(NumberUtils.toInt(array[9]));
        Ask[] asks = new Ask[10];
        Bid[] bids = new Bid[10];
        for (int i = 0; i < 10; i++) {
            asks[i] = new Ask();
            asks[i].setPrice(NumberUtils.toInt(array[10+i]));
            asks[i].setVolume(NumberUtils.toInt(array[20+i]));

            bids[i] = new Bid();
            bids[i].setPrice(NumberUtils.toInt(array[30+i]));
            bids[i].setVolume(NumberUtils.toInt(array[40+i]));
        }

        marketData.setAsks(asks);
        marketData.setBids(bids);

        marketData.setVolume(NumberUtils.toLong(array[51]));
        marketData.setTurnover(NumberUtils.toLong(array[52]));
        marketData.setUpLimit(NumberUtils.toInt(array[57]));
        marketData.setDownLimit(NumberUtils.toInt(array[58]));

        return marketData;
    }

    // 解压缩
    public static byte[] uncompress(byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            return data;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        GZIPInputStream gunzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        gunzip.close();
        in.close();
        return out.toByteArray();
    }

}
