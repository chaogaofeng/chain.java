package com.glodnet.chain;

import com.glodnet.chain.util.JsonToProtoObjectUtil;
import com.google.protobuf.util.JsonFormat;
import cosmos.auth.v1beta1.QueryOuterClass.*;
import cosmos.auth.v1beta1.Auth.*;
import org.junit.Test;

public class AuthTest {
    private static final JsonFormat.Printer printer = JsonToProtoObjectUtil.getPrinter();
    static HttpClient client;
    static {
        client = TestUtils.generateClient();
    }

    @Test
    public void testAccount() throws Exception {
        QueryAccountResponse response = QueryAuth.getAccount(client, "gnc1azlj5whn5rm2xtqeekkdqgwg7036naf0sfqwmu");
        System.out.println(printer.print(response));
    }

    @Test
    public void testBaseAccount() throws Exception {
        BaseAccount response = QueryAuth.getBaseAccount(client, "gnc1azlj5whn5rm2xtqeekkdqgwg7036naf0sfqwmu");
        System.out.println(printer.print(response));
    }

    @Test
    public void testAccounts() throws Exception {
        QueryAccountsResponse response = QueryAuth.getAccounts(client, "");
        System.out.println(printer.print(response));
    }
}
