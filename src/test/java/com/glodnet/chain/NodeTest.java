package com.glodnet.chain;
import com.glodnet.chain.util.JsonToProtoObjectUtil;
import com.google.protobuf.util.JsonFormat;
import cosmos.base.tendermint.v1beta1.Query;
import cosmos.tx.v1beta1.ServiceOuterClass;
import org.junit.Test;

import java.util.ArrayList;

public class NodeTest {
    private static final JsonFormat.Printer printer = JsonToProtoObjectUtil.getPrinter();
    static HttpClient client;
    static {
        client = TestUtils.generateClient();
    }

    @Test
    public void testNodeInfo() throws Exception {
        Query.GetNodeInfoResponse response = Node.getNodeInfo(client);
        System.out.println(printer.print(response));
    }

    @Test
    public void testSyncing() throws Exception {
        Query.GetSyncingResponse response = Node.getSyncing(client);
        System.out.println(printer.print(response));
    }

    @Test
    public void testLatestBlock() throws Exception {
        Query.GetLatestBlockResponse response = Node.getLatestBlock(client);
        System.out.println(printer.print(response));
    }

    @Test
    public void testBlockByHeight() throws Exception {
        Query.GetBlockByHeightResponse response = Node.getBlockByHeight(client, 1L);
        System.out.println(printer.print(response));
    }

    @Test
    public void testLatestValidatorSet() throws Exception {
        Query.GetLatestValidatorSetResponse response = Node.getLatestValidatorSet(client, "");
        System.out.println(printer.print(response));
    }

    @Test
    public void testValidatorSetByHeight() throws Exception {
        Query.GetValidatorSetByHeightResponse response = Node.getValidatorSetByHeight(client, 1L, "");
        System.out.println(printer.print(response));
    }

    @Test
    public void testTx() throws Exception {
        String hash = "180721A7C0C34FBCDCF20CCC235379088AB468BBE1B7B3B65E733D3573E11964";
        ServiceOuterClass.GetTxResponse response = Node.getTx(client, hash);
        System.out.println(printer.print(response));
    }

    @Test
    public void testTxsByEvent() throws Exception {
        ArrayList<String> events = new ArrayList<>();
        events.add("message.action='/cosmos.bank.v1beta1.MsgSend'");
        ServiceOuterClass.GetTxsEventResponse response = Node.getTxsByEvent(client, events, "");
        System.out.println(printer.print(response));
    }

    @Test
    public void testTxsByHeight() throws Exception {
        ServiceOuterClass.GetTxsEventResponse response = Node.getTxsByHeight(client, 103L, "");
        System.out.println(printer.print(response));
    }
}
