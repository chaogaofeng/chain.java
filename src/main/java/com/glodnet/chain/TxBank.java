package com.glodnet.chain;

import com.google.protobuf.Any;
import cosmos.bank.v1beta1.Tx.*;
import cosmos.base.v1beta1.CoinOuterClass.Coin;

import java.util.List;

public class TxBank {
    /**
     * 创建消息结构---转账
     *
     * @param from    发送者地址
     * @param to      接收者地址
     * @param amounts 金额
     * @return MsgStoreCode对象
     */
    public static Any NewMsgSend(String from, String to, List<Coin> amounts) throws Exception {
        MsgSend msg = MsgSend.newBuilder()
                .setFromAddress(from)
                .setToAddress(to)
                .addAllAmount(amounts)
                .build();
        return Any.pack(msg, "/");
    }
}
