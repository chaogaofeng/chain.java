package com.glodnet.chain;

/**
 * TestUtils provides a simplified api for testing.
 */
public class TestUtils {
    public static HttpClient generateClient()  {
        return new HttpClient("http://127.0.0.1:1317");
    }
}
