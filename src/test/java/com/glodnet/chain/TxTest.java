package com.glodnet.chain;

import com.glodnet.chain.keys.IKeyService;
import com.glodnet.chain.util.SignAlgo;
import com.google.protobuf.Any;
import cosmos.base.v1beta1.CoinOuterClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TxTest {
    static HttpClient client;
    static {
        client = TestUtils.generateClient();
    }

    @Test
    public void testSendTx() throws Exception {
        Tx tx = new Tx(client, null,  SignAlgo.SECP256K1, "gnchain", CoinOuterClass.DecCoin.newBuilder().setAmount("0.00002").setDenom("ugnc").build(), new BigDecimal(1.1));
        // 私钥生成公钥、地址
        String mnemonic = "apology false junior asset sphere puppy upset dirt miracle rice horn spell ring vast wrist crisp snake oak give cement pause swallow barely clever";
        IKeyService keyService = tx.getKeyService();
        keyService.recoverKey("alice", "123456", mnemonic, true, 0, "");
        String address = keyService.showAddress("alice");

        List<CoinOuterClass.Coin> amounts = new ArrayList<>();
        amounts.add(CoinOuterClass.Coin.newBuilder().setDenom("ugnc").setAmount("100").build());

        List<Any> msgs = new ArrayList<>();
        msgs.add(TxBank.NewMsgSend(address, address, amounts));

        // 生成、签名、广播交易
        String hash = tx.send("alice", "123456", BuildTxOptions.builder()
                .sender(address)
                .msgs(msgs)
                .memo("")
                .build());
        System.out.println(hash);
    }

    @Test
    public void testSendTx2() throws Exception {
        Tx tx = new Tx(client, null, SignAlgo.SM2, "gnchain", CoinOuterClass.DecCoin.newBuilder().setAmount("0.00002").setDenom("ugnc").build(), new BigDecimal(1.1));
        // 私钥生成公钥、地址
        String mnemonic = "apology false junior asset sphere puppy upset dirt miracle rice horn spell ring vast wrist crisp snake oak give cement pause swallow barely clever";
        IKeyService keyService = tx.getKeyService();
        keyService.recoverKey("alice", "123456", mnemonic, true, 0, "");
        String address = keyService.showAddress("alice");

        List<CoinOuterClass.Coin> amounts = new ArrayList<>();
        amounts.add(CoinOuterClass.Coin.newBuilder().setDenom("ugnc").setAmount("100").build());

        List<Any> msgs = new ArrayList<>();
        msgs.add(TxBank.NewMsgSend(address, address, amounts));

        // 生成、签名、广播交易
        String hash = tx.send("alice", "123456", BuildTxOptions.builder()
                .sender(address)
                .msgs(msgs)
                .memo("")
                .build());
        System.out.println(hash);
    }
}
