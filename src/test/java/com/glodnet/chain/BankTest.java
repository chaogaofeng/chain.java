package com.glodnet.chain;

import com.glodnet.chain.util.JsonToProtoObjectUtil;
import com.google.protobuf.util.JsonFormat;
import cosmos.bank.v1beta1.QueryOuterClass.*;
import org.junit.Test;

public class BankTest {
    private static final JsonFormat.Printer printer = JsonToProtoObjectUtil.getPrinter();
    static HttpClient client;
    static {
        client = TestUtils.generateClient();
    }

    @Test
    public void testBalance() throws Exception {
        QueryBalanceResponse response = QueryBank.getBalance(client, "gnc1azlj5whn5rm2xtqeekkdqgwg7036naf0sfqwmu", "ugnc");
        System.out.println(printer.print(response));
    }

    @Test
    public void testBalances() throws Exception {
        QueryAllBalancesResponse response = QueryBank.getBalances(client, "gnc1azlj5whn5rm2xtqeekkdqgwg7036naf0sfqwmu");
        System.out.println(printer.print(response));
    }

    @Test
    public void testSupplyOf() throws Exception {
        QuerySupplyOfResponse response = QueryBank.getSupplyOf(client, "ugnc");
        System.out.println(printer.print(response));
    }

    @Test
    public void testTotalSupply() throws Exception {
        QueryTotalSupplyResponse response = QueryBank.getTotalSupply(client, "");
        System.out.println(printer.print(response));
    }

    @Test
    public void testDenomMetadata() throws Exception {
        QueryDenomMetadataResponse response = QueryBank.getDenomMetadata(client, "ugnc");
        System.out.println(printer.print(response));
    }

    @Test
    public void testDenomsMetadata() throws Exception {
        QueryDenomsMetadataResponse response = QueryBank.getDenomsMetadata(client, "");
        System.out.println(printer.print(response));
    }
}
