package com.glodnet.chain.util;

import org.bouncycastle.util.encoders.Base64;

public class Key {
    private String address;
    private byte[] pubKey;
    private byte[] privKey;

    public Key(String address, byte[] pubKey, byte[] privKey) {
        this.address = address;
        this.pubKey = pubKey;
        this.privKey = privKey;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public byte[] getPrivKey() {
        return privKey;
    }

    public byte[] getPubKey() {
        return pubKey;
    }

    public void setPrivKey(byte[] privKey) {
        this.privKey = privKey;
    }

    @Override
    public String toString() {
        return "Key{" +
                "address='" + address + '\'' +
                ", privKey='" + Base64.toBase64String(privKey) + '\'' +
                '}';
    }
}
