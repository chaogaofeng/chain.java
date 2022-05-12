package com.glodnet.chain;

import com.glodnet.chain.util.JsonToProtoObjectUtil;
import com.google.protobuf.util.JsonFormat;
import cosmwasm.wasm.v1.QueryOuterClass.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.bouncycastle.util.encoders.Hex;

public class QueryWasm {
    private static final JsonFormat.Printer printer = JsonToProtoObjectUtil.getPrinter();
    /**
     * 合约代码列表
     */
    public static QueryCodesResponse getCodes(HttpClient client, String nextKey) throws Exception {
        String path = String.format("/cosmwasm/wasm/v1/code");
        MultiValuedMap<String, String> queryMap = new ArrayListValuedHashMap<>();
        queryMap.put("pagination.key", nextKey);
        return client.get(path, queryMap, QueryCodesResponse.class);
    }

    /**
     * Pinned合约代码列表
     */
    public static QueryPinnedCodesResponse getPinnedCodes(HttpClient client, String nextKey) throws Exception {
        String path = String.format("/cosmwasm/wasm/v1/codes/pinned");
        MultiValuedMap<String, String> queryMap = new ArrayListValuedHashMap<>();
        queryMap.put("pagination.key", nextKey);
        return client.get(path, queryMap, QueryPinnedCodesResponse.class);
    }

    /**
     * 合约代码详情
     */
    public static QueryCodeResponse getCode(HttpClient client, Long codeID) throws Exception {
        String path = String.format("/cosmwasm/wasm/v1/code/"+codeID);
        return client.get(path, QueryCodeResponse.class);
    }

    /**
     * 指定合约代码的已部署合约列表
     */
    public static QueryContractsByCodeResponse getContractsByCode(HttpClient client, Long codeID, String nextKey) throws Exception {
        String path = String.format("/cosmwasm/wasm/v1/code/"+codeID+"/contracts");
        MultiValuedMap<String, String> queryMap = new ArrayListValuedHashMap<>();
        queryMap.put("pagination.key", nextKey);
        return client.get(path, queryMap, QueryContractsByCodeResponse.class);
    }

    /**
     * 已部署合约信息
     */
    public static QueryContractInfoResponse getContractInfo(HttpClient client, String address) throws Exception {
        String path = String.format("/cosmwasm/wasm/v1/contract/"+address);
        return client.get(path, QueryContractInfoResponse.class);
    }

    /**
     * 已部署合约信息
     */
    public static QueryContractHistoryResponse getContractHistory(HttpClient client, String address, String nextKey) throws Exception {
        String path = String.format("/cosmwasm/wasm/v1/contract/"+address+"/history");
        MultiValuedMap<String, String> queryMap = new ArrayListValuedHashMap<>();
        queryMap.put("pagination.key", nextKey);
        return client.get(path, queryMap, QueryContractHistoryResponse.class);
    }

    /**
     * 已部署合约状态数据
     */
    public static QueryAllContractStateResponse getAllContractState(HttpClient client, String address, String nextKey) throws Exception {
        String path = String.format("/cosmwasm/wasm/v1/contract/"+address+"/state");
        MultiValuedMap<String, String> queryMap = new ArrayListValuedHashMap<>();
        queryMap.put("pagination.key", nextKey);
        return client.get(path, queryMap, QueryAllContractStateResponse.class);
    }

    /**
     * 已部署合约状态数据
     */
    public static QueryRawContractStateResponse getRawContractStateHex(HttpClient client, String address, String queryDataHex) throws Exception {
        String path = String.format("/cosmwasm/wasm/v1/contract/"+address+"/raw/"+Base64.encodeBase64String(Hex.decode(queryDataHex)));
        return client.get(path, QueryRawContractStateResponse.class);
    }

    /**
     * 已部署合约状态数据
     */
    public static QueryRawContractStateResponse getRawContractState(HttpClient client, String address, String key, String ...others) throws Exception {
        String queryDataHex = String.format("%04x", key.length())+Hex.toHexString(key.getBytes());
        for (String other:others) {
            queryDataHex += Hex.toHexString(other.getBytes());
        }
        return getRawContractStateHex(client, address, queryDataHex);
    }

    /**
     * 已部署合约状态数据查询
     */
    public static QuerySmartContractStateResponse getSmartContractState(HttpClient client, String address, String queryData) throws Exception {
        String path = String.format("/cosmwasm/wasm/v1/contract/"+address+"/smart/"+Base64.encodeBase64URLSafeString(queryData.getBytes()));
        return client.get(path, QuerySmartContractStateResponse.class);
    }
}
