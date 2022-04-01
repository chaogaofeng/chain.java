package com.glodnet.chain;

import cosmos.bank.v1beta1.QueryOuterClass.*;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

public class QueryBank {
    public static QueryBalanceResponse getBalance(HttpClient client, String address, String denom) throws Exception {
        String path = String.format("/cosmos/bank/v1beta1/balances/"+address+"/by_denom");
        MultiValuedMap<String, String> queryMap = new ArrayListValuedHashMap<>();
        queryMap.put("denom", denom);
        return client.get(path, queryMap, QueryBalanceResponse.class);
    }

    public static QueryAllBalancesResponse getBalances(HttpClient client, String address) throws Exception {
        String path = String.format("/cosmos/bank/v1beta1/balances/"+address);
        return client.get(path, QueryAllBalancesResponse.class);
    }

    public static QueryTotalSupplyResponse getTotalSupply(HttpClient client, String nextKey) throws Exception {
        String path = String.format("/cosmos/bank/v1beta1/supply");
        MultiValuedMap<String, String> queryMap = new ArrayListValuedHashMap<>();
        queryMap.put("pagination.key", nextKey);
        return client.get(path, queryMap, QueryTotalSupplyResponse.class);
    }

    public static QuerySupplyOfResponse getSupplyOf(HttpClient client, String denom) throws Exception {
        String path = String.format("/cosmos/bank/v1beta1/supply/"+denom);
        return client.get(path, QuerySupplyOfResponse.class);
    }

    public static QueryDenomsMetadataResponse getDenomsMetadata(HttpClient client, String nextKey) throws Exception {
        String path = String.format("/cosmos/bank/v1beta1/denoms_metadata");
        MultiValuedMap<String, String> queryMap = new ArrayListValuedHashMap<>();
        queryMap.put("pagination.key", nextKey);
        return client.get(path, queryMap, QueryDenomsMetadataResponse.class);
    }

    public static QueryDenomMetadataResponse getDenomMetadata(HttpClient client, String denom) throws Exception {
        String path = String.format("/cosmos/bank/v1beta1/denoms_metadata/"+denom);
        return client.get(path, QueryDenomMetadataResponse.class);
    }
}
