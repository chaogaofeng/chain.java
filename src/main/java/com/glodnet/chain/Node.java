package com.glodnet.chain;

import com.glodnet.chain.util.JsonToProtoObjectUtil;
import com.google.protobuf.util.JsonFormat;
import cosmos.base.tendermint.v1beta1.Query;
import cosmos.tx.v1beta1.ServiceOuterClass;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.util.ArrayList;

public class Node {
    private static final JsonFormat.Printer printer = JsonToProtoObjectUtil.getPrinter();

    public static Query.GetNodeInfoResponse getNodeInfo(HttpClient client) throws Exception {
        String path = String.format("/cosmos/base/tendermint/v1beta1/node_info");
        return client.get(path, Query.GetNodeInfoResponse.class);
    }

    public static Query.GetSyncingResponse getSyncing(HttpClient client) throws Exception {
        String path = String.format("/cosmos/base/tendermint/v1beta1/syncing");
        return client.get(path, Query.GetSyncingResponse.class);
    }

    public static Query.GetLatestBlockResponse getLatestBlock(HttpClient client) throws Exception {
        String path = String.format("/cosmos/base/tendermint/v1beta1/blocks/latest");
        return client.get(path, Query.GetLatestBlockResponse.class);
    }

    public static Query.GetBlockByHeightResponse getBlockByHeight(HttpClient client, Long height) throws Exception {
        String path = String.format("/cosmos/base/tendermint/v1beta1/blocks/%d", height);
        return client.get(path, Query.GetBlockByHeightResponse.class);
    }

    public static Query.GetLatestValidatorSetResponse getLatestValidatorSet(HttpClient client, String nextKey) throws Exception {
        String path = String.format("/cosmos/base/tendermint/v1beta1/validatorsets/latest");
        MultiValuedMap<String, String> queryMap = new ArrayListValuedHashMap<>();
        queryMap.put("pagination.key", nextKey);
        return client.get(path, queryMap, Query.GetLatestValidatorSetResponse.class);
    }

    public static Query.GetValidatorSetByHeightResponse getValidatorSetByHeight(HttpClient client, Long height, String nextKey) throws Exception {
        String path = String.format("/cosmos/base/tendermint/v1beta1/validatorsets/%d", height);
        MultiValuedMap<String, String> queryMap = new ArrayListValuedHashMap<>();
        queryMap.put("pagination.key", nextKey);
        return client.get(path, queryMap, Query.GetValidatorSetByHeightResponse.class);
    }

    public static ServiceOuterClass.GetTxResponse getTx(HttpClient client, String hash) throws Exception {
        String path = String.format("/cosmos/tx/v1beta1/txs/%s", hash);
        return client.get(path, ServiceOuterClass.GetTxResponse.class);
    }

    public static ServiceOuterClass.GetTxsEventResponse getTxsByEvent(HttpClient client, ArrayList<String> events, String nextKey) throws Exception {
        MultiValuedMap<String, String> queryMap = new ArrayListValuedHashMap<>();
        for (String event :events) {
            queryMap.put("events", event);
        }
        queryMap.put("pagination.key", nextKey);
        ServiceOuterClass.GetTxsEventResponse eventResponse = client.get("/cosmos/tx/v1beta1/txs", queryMap, ServiceOuterClass.GetTxsEventResponse.class);
        return eventResponse;
    }

    public static ServiceOuterClass.GetTxsEventResponse getTxsByHeight(HttpClient client, Long height, String nextKey) throws Exception {
        ArrayList<String> events = new ArrayList<>();
        events.add("tx.height=" + height);
        return getTxsByEvent(client, events, nextKey);
    }

    public static ServiceOuterClass.SimulateResponse simulate(HttpClient client, ServiceOuterClass.SimulateRequest req) throws Exception {
        String reqBody = printer.print(req);
        ServiceOuterClass.SimulateResponse simulateResponse = client.post("/cosmos/tx/v1beta1/simulate", reqBody, ServiceOuterClass.SimulateResponse.class);
        return simulateResponse;
    }

    public static ServiceOuterClass.BroadcastTxResponse broadcastTx(HttpClient client, ServiceOuterClass.BroadcastTxRequest req) throws Exception {
        String reqBody = printer.print(req);
        ServiceOuterClass.BroadcastTxResponse broadcastTxResponse = client.post("/cosmos/tx/v1beta1/txs", reqBody, ServiceOuterClass.BroadcastTxResponse.class);
        return broadcastTxResponse;
    }
}
