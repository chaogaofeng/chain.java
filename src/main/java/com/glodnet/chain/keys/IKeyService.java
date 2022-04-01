package com.glodnet.chain.keys;

import com.glodnet.chain.exception.KeyException;
import com.glodnet.chain.util.Key;
import com.glodnet.chain.util.Mnemonic;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import cosmos.auth.v1beta1.Auth;
import cosmos.base.v1beta1.CoinOuterClass;
import cosmos.tx.signing.v1beta1.Signing;
import cosmos.tx.v1beta1.TxOuterClass;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.math.ec.ECPoint;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

/**
 * Key management service
 */
public interface IKeyService {

    /**
     * Creates a new key
     *
     * @param name     Name of the key
     * @param password Password for encrypting the keystore
     * @return Bech32 address and mnemonic
     */
    Mnemonic addKey(String name, String password) throws KeyException;

    /**
     * Recovers a key
     *
     * @param name         Name of the key
     * @param password     Password for encrypting the keystore
     * @param mnemonic     Mnemonic of the key
     * @param derive       Derive a private key using the default HD path (default: true)
     * @param index        The bip44 address index (default: 0)
     * @param saltPassword A passphrase for generating the salt, according to bip39
     * @return Bech32 address
     */
    String recoverKey(String name, String password, String mnemonic, boolean derive, int index, String saltPassword) throws KeyException;

    /**
     * Imports a key from keystore
     *
     * @param name             Name of the key
     * @param keyPassword      Password of the key
     * @param keystorePassword Password for encrypting the keystore
     * @param keystore         Keystore json
     * @return Bech32 address
     */
    String importFromKeystore(String name, String keyPassword, String keystorePassword, String keystore) throws KeyException, IOException;

    /**
     * Exports keystore of a key
     *
     * @param name                 Name of the key
     * @param keyPassword          Password of the key
     * @param keystorePassword     Password for encrypting the keystore
     * @param destinationDirectory Directory for Keystore file to export
     * @return Keystore json
     */
    String exportKeystore(String name, String keyPassword, String keystorePassword, File destinationDirectory) throws KeyException, IOException;

    /**
     * Deletes a key
     *
     * @param name     Name of the key
     * @param password Password of the key
     */
    void deleteKey(String name, String password) throws KeyException;

    /**
     * Gets address of a key
     *
     * @param name Name of the key
     * @return Bech32 address
     */
    String showAddress(String name) throws KeyException;

    /**
     * Get privKey by name
     *
     * @param name     Name of the key
     * @param password Password of the key
     * @return {@link Key}
     * @throws KeyException If error occurs
     */
    Key getKey(String name, String password) throws KeyException;

    /**
     * Get privKey by name
     *
     * @param name     Name of the key
     * @param password Password of the key
     * @param signdoc  content of the signed
     * @return signed
     * @throws KeyException If error occurs
     */
    byte[] sign(String name, String password, byte[] signdoc) throws KeyException, CryptoException;

    /**
     * Single sign a transaction
     *
     * @param tx   TxBody to be signed
     * @param name     Name of the key to sign the tx
     * @param password Password of the key
     * @param overwriteSig  overwrite signing, default `false`
     * @return The signed tx
     * @throws KeyException if the signing failed
     */
    TxOuterClass.Tx signTx(TxOuterClass.Tx tx, String name, String password, String chainID,  Long accountNumber, Long sequence, boolean overwriteSig) throws KeyException, IOException, CryptoException;
}
