package com.glodnet.chain;

import cosmos.auth.v1beta1.QueryOuterClass.*;
import cosmos.auth.v1beta1.Auth.*;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

public class QueryAuth {
    public static QueryAccountResponse getAccount(HttpClient client, String address) throws Exception {
        String path = String.format("/cosmos/auth/v1beta1/accounts/"+address);
        return client.get(path, QueryAccountResponse.class);
    }

    public static BaseAccount getBaseAccount(HttpClient client, String address) throws Exception {
        QueryAccountResponse response = getAccount(client, address);
        if (response.hasAccount() && response.getAccount().is(BaseAccount.class)) {
            return response.getAccount().unpack(BaseAccount.class);
        }
        throw new RuntimeException("base account not found:" + address);
    }

    public static QueryAccountsResponse getAccounts(HttpClient client, String nextKey) throws Exception {
        String path = String.format("/cosmos/auth/v1beta1/accounts");
        MultiValuedMap<String, String> queryMap = new ArrayListValuedHashMap<>();
        queryMap.put("pagination.key", nextKey);
        return client.get(path, queryMap, QueryAccountsResponse.class);
    }
}
