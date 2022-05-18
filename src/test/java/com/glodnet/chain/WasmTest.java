package com.glodnet.chain;

import com.glodnet.chain.keys.IKeyService;
import com.glodnet.chain.util.JsonToProtoObjectUtil;
import com.glodnet.chain.util.SignAlgo;
import com.google.protobuf.Any;
import com.google.protobuf.util.JsonFormat;
import cosmos.base.v1beta1.CoinOuterClass;
import cosmwasm.wasm.v1.QueryOuterClass.*;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class WasmTest {
    private static final JsonFormat.Printer printer = JsonToProtoObjectUtil.getPrinter();
    static HttpClient client;
    static {
        client = TestUtils.generateClient();
    }

    @Test
    public void testStoreCode() throws Exception {
        Tx tx = new Tx(client, null, SignAlgo.SM2,"gnchain_45-1", CoinOuterClass.DecCoin.newBuilder().setAmount("0.00002").setDenom("ugnc").build(), new BigDecimal(1.1));

        // 私钥生成公钥、地址
        String mnemonic = "apology false junior asset sphere puppy upset dirt miracle rice horn spell ring vast wrist crisp snake oak give cement pause swallow barely clever";
        IKeyService keyService = tx.getKeyService();
        keyService.recoverKey("alice", "123456", mnemonic, true, 0, "");
        String address = keyService.showAddress("alice");

        List<Any> msgs = new ArrayList<>();
        msgs.add(TxWasm.NewMsgStoreCode(address, "cw_nameservice.wasm", ""));

        // 生成、签名、广播交易
        String hash = tx.send("alice", "123456", BuildTxOptions.builder()
                .sender(address)
                .msgs(msgs)
                .memo("")
                .build());
        System.out.println(hash);
    }

    @Test
    public void testInstantiateContract() throws Exception {
        Tx tx = new Tx(client, null, SignAlgo.SECP256K1,"gnchain", CoinOuterClass.DecCoin.newBuilder().setAmount("0.00002").setDenom("ugnc").build(), new BigDecimal(1.3));

        // 私钥生成公钥、地址
        String mnemonic = "apology false junior asset sphere puppy upset dirt miracle rice horn spell ring vast wrist crisp snake oak give cement pause swallow barely clever";
        IKeyService keyService = tx.getKeyService();
        keyService.recoverKey("alice", "123456", mnemonic, true, 0, "");
        String address = keyService.showAddress("alice");

        List<Any> msgs = new ArrayList<>();
        String initArgs = "{\"purchase_price\":{\"amount\":\"100\",\"denom\":\"ugnc\"},\"transfer_price\":{\"amount\":\"999\",\"denom\":\"ugnc\"}}";
        msgs.add(TxWasm.NewMsgInstantiateContract(address, 15L, initArgs, "", "", new ArrayList<>()));

        // 生成、签名、广播交易
        String hash = tx.send("alice", "123456", BuildTxOptions.builder()
                .sender(address)
                .msgs(msgs)
                .memo("")
                .build());
        System.out.println(hash);
    }

    @Test
    public void testExecuteContract() throws Exception {
        Tx tx = new Tx(client, null, SignAlgo.SECP256K1,"gnchain", CoinOuterClass.DecCoin.newBuilder().setAmount("0.00002").setDenom("ugnc").build(), new BigDecimal(1.3));

        // 私钥生成公钥、地址
        String mnemonic = "apology false junior asset sphere puppy upset dirt miracle rice horn spell ring vast wrist crisp snake oak give cement pause swallow barely clever";
        IKeyService keyService = tx.getKeyService();
        keyService.recoverKey("alice", "123456", mnemonic, true, 0, "");
        String address = keyService.showAddress("alice");

        List<CoinOuterClass.Coin> amounts = new ArrayList<>();
        amounts.add(CoinOuterClass.Coin.newBuilder().setDenom("ugnc").setAmount("100").build());

        List<Any> msgs = new ArrayList<>();
        String execArgs = "{\"register\":{\"name\":\"fred\"}}";
        msgs.add(TxWasm.NewMsgExecuteContract(address, "gnc1ul4msjc3mmaxsscdgdtjds85rg50qrepvrczp0ldgma5mm9xv8yqxcxckv",execArgs, amounts));

        // 生成、签名、广播交易
        String hash = tx.send("alice", "123456", BuildTxOptions.builder()
                .sender(address)
                .msgs(msgs)
                .memo("")
                .build());
        System.out.println(hash);
    }

    @Test
    public void testExecuteContract2() throws Exception {
        Tx tx = new Tx(client, null, SignAlgo.SECP256K1,"gnchain", CoinOuterClass.DecCoin.newBuilder().setAmount("0.00002").setDenom("ugnc").build(), new BigDecimal(1.3));

        // 私钥生成公钥、地址
        String mnemonic = "apology false junior asset sphere puppy upset dirt miracle rice horn spell ring vast wrist crisp snake oak give cement pause swallow barely clever";
        IKeyService keyService = tx.getKeyService();
        keyService.recoverKey("alice", "123456", mnemonic, true, 0, "");
        String address = keyService.showAddress("alice");

        List<CoinOuterClass.Coin> amounts = new ArrayList<>();
        amounts.add(CoinOuterClass.Coin.newBuilder().setDenom("ugnc").setAmount("999").build());

        List<Any> msgs = new ArrayList<>();
        String execArgs = "{\"transfer\":{\"name\":\"fred\",\"to\":\"gnc1azlj5whn5rm2xtqeekkdqgwg7036naf0sfqwmu\"}}";
        msgs.add(TxWasm.NewMsgExecuteContract(address, "gnc1ul4msjc3mmaxsscdgdtjds85rg50qrepvrczp0ldgma5mm9xv8yqxcxckv",execArgs, amounts));

        // 生成、签名、广播交易
        String hash = tx.send("alice", "123456", BuildTxOptions.builder()
                .sender(address)
                .msgs(msgs)
                .memo("")
                .build());
        System.out.println(hash);
    }



    @Test
    public void testCodes() throws Exception {
        QueryCodesResponse response = QueryWasm.getCodes(client, "");
        System.out.println(printer.print(response));
    }

    @Test
    public void testCode() throws Exception {
        QueryCodeResponse response = QueryWasm.getCode(client, 1L);
        System.out.println(printer.print(response));
    }

    @Test
    public void testContractsByCode() throws Exception {
        QueryContractsByCodeResponse response = QueryWasm.getContractsByCode(client, 1L, "");
        System.out.println(printer.print(response));
    }

    @Test
    public void testContractInfo() throws Exception {
        String address = "gnc18a0pvw326fydfdat5tzyf4t8lhz0v6fyfaujpeg07fwqkygcxejspht8l4";
        QueryContractInfoResponse response = QueryWasm.getContractInfo(client, address);
        System.out.println(printer.print(response));
    }

    @Test
    public void testContractHistory() throws Exception {
        String address = "gnc18a0pvw326fydfdat5tzyf4t8lhz0v6fyfaujpeg07fwqkygcxejspht8l4";
        QueryContractHistoryResponse response = QueryWasm.getContractHistory(client, address, "");
        System.out.println(printer.print(response));
    }

    @Test
    public void testContractAllState() throws Exception {
        String address = "gnc18a0pvw326fydfdat5tzyf4t8lhz0v6fyfaujpeg07fwqkygcxejspht8l4";
        QueryAllContractStateResponse response = QueryWasm.getAllContractState(client, address, "");
        System.out.println(printer.print(response));
    }

    @Test
    public void testContractRawStateHex() throws Exception {
        String address = "gnc18a0pvw326fydfdat5tzyf4t8lhz0v6fyfaujpeg07fwqkygcxejspht8l4";
        QueryRawContractStateResponse response = QueryWasm.getRawContractStateHex(client, address, "000C6E616D657265736F6C76657266726564");
        System.out.println(printer.print(response));
    }

    @Test
    public void testContractRawState() throws Exception {
        String address = "gnc18a0pvw326fydfdat5tzyf4t8lhz0v6fyfaujpeg07fwqkygcxejspht8l4";
        QueryRawContractStateResponse response = QueryWasm.getRawContractState(client, address, "config");
        System.out.println(printer.print(response));
    }

    @Test
    public void testContractRawState2() throws Exception {
        String address = "gnc18a0pvw326fydfdat5tzyf4t8lhz0v6fyfaujpeg07fwqkygcxejspht8l4";
        QueryRawContractStateResponse response = QueryWasm.getRawContractState(client, address, "nameresolver", "fred");
        System.out.println(printer.print(response));
    }

    @Test
    public void testContractSmartState() throws Exception {
        String address = "gnc18a0pvw326fydfdat5tzyf4t8lhz0v6fyfaujpeg07fwqkygcxejspht8l4";
        QuerySmartContractStateResponse response = QueryWasm.getSmartContractState(client, address, "{\"resolve_record\": {\"name\": \"fred\"}}");
        System.out.println(printer.print(response));
    }
}
