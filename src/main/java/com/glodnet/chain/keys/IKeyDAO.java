package com.glodnet.chain.keys;

import com.glodnet.chain.exception.KeyException;
import com.glodnet.chain.util.AESUtils;
import com.glodnet.chain.util.Key;

/**
 * Key DAO Interface, to be implemented by apps if they need the key management.
 */
public interface IKeyDAO {

    /**
     * Save the encrypted private key to app
     *
     * @param name Name of the key
     * @param key The encrypted private key object
     * @throws KeyException if the save fails.
     */
    void write(String name, Key key) throws KeyException;

    /**
     * Get the encrypted private key by name
     *
     * @param name Name of the key
     * @return The encrypted private key object or null
     */
    Key read(String name);

    /**
     * Delete the key by name
     * @param name Name of the key
     * @throws KeyException if the deletion fails.
     */
    void delete(String name) throws KeyException;

    /**
     * Optional function to encrypt the private key by yourself. Default to AES Encryption
     *
     * @param privKey The plain private key
     * @param password The password to encrypt the private key
     * @return The encrypted private key
     * @throws KeyException if encrypt failed
     */
    default byte[] encrypt(byte[] privKey, String password) throws KeyException {
        try {
            return AESUtils.encrypt(privKey, password);
        } catch (Exception e) {
            e.printStackTrace();
            throw new KeyException("Private key encrypt failed");
        }
    }

    /**
     * Optional function to decrypt the private key by yourself. Default to AES Decryption
     *
     * @param encrptedPrivKey The encrpted private key
     * @param password The password to decrypt the private key
     * @return The plain private key
     * @throws KeyException if decrypt failed
     */
    default byte[] decrypt(byte[] encrptedPrivKey, String password) throws KeyException {
        try {
            return AESUtils.decrypt(encrptedPrivKey, password);
        } catch (Exception e) {
            e.printStackTrace();
            throw new KeyException("Private key decrypt failed");
        }
    }
}
