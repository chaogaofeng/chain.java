package com.glodnet.chain;

import com.glodnet.chain.keys.IKeyService;
import com.glodnet.chain.util.JsonToProtoObjectUtil;
import com.glodnet.chain.util.SignAlgo;
import com.google.protobuf.Any;
import com.google.protobuf.util.JsonFormat;
import cosmos.base.v1beta1.CoinOuterClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StakingTest {
    private static final JsonFormat.Printer printer = JsonToProtoObjectUtil.getPrinter();
    static HttpClient client;
    static {
        client = TestUtils.generateClient();
    }

    @Test
    public void testCreateValidator() throws Exception {
        Tx tx = new Tx(client, null, SignAlgo.SM2,"gnchain_45-1", CoinOuterClass.DecCoin.newBuilder().setAmount("0.00002").setDenom("ugnc").build(), new BigDecimal(1.1));

        // 私钥生成公钥、地址
        String mnemonic = "apology false junior asset sphere puppy upset dirt miracle rice horn spell ring vast wrist crisp snake oak give cement pause swallow barely clever";
        IKeyService keyService = tx.getKeyService();
        keyService.recoverKey("alice", "123456", mnemonic, true, 0, "");
        String address = keyService.showAddress("alice");

        List<Any> msgs = new ArrayList<>();
        msgs.add(TxStaking.NewMsgCreateValidator(address, keyService.PubKey("alice"), CoinOuterClass.Coin.newBuilder().setAmount("1000000").setDenom("ugnc").build(), "ddd", ""));

        // 生成、签名、广播交易
        String hash = tx.send("alice", "123456", BuildTxOptions.builder()
                .sender(address)
                .msgs(msgs)
                .memo("")
                .build());
        System.out.println(hash);
    }

    @Test
    public void testUndelegate() throws Exception {
        Tx tx = new Tx(client, null, SignAlgo.SM2,"gnchain_45-1", CoinOuterClass.DecCoin.newBuilder().setAmount("0.00002").setDenom("ugnc").build(), new BigDecimal(1.1));

        // 私钥生成公钥、地址
        String mnemonic = "apology false junior asset sphere puppy upset dirt miracle rice horn spell ring vast wrist crisp snake oak give cement pause swallow barely clever";
        IKeyService keyService = tx.getKeyService();
        keyService.recoverKey("alice", "123456", mnemonic, true, 0, "");
        String address = keyService.showAddress("alice");

        List<Any> msgs = new ArrayList<>();
        msgs.add(TxStaking.NewMsgUndelegate(address, CoinOuterClass.Coin.newBuilder().setAmount("1000000").setDenom("ugnc").build()));

        // 生成、签名、广播交易
        String hash = tx.send("alice", "123456", BuildTxOptions.builder()
                .sender(address)
                .msgs(msgs)
                .memo("")
                .build());
        System.out.println(hash);
    }
}
