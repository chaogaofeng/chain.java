package com.glodnet.chain;

/**
 * TestUtils provides a simplified api for testing.
 */
public class TestUtils {
    public static HttpClient generateClient()  {
        return new HttpClient("http://10.1.120.35:30488/testnode-gnchaind/api");
    }
}
