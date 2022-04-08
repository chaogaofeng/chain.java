package com.glodnet.chain.keys.impl;

import com.codahale.xsalsa20poly1305.SimpleBox;
import com.glodnet.chain.util.Key;
import com.glodnet.chain.util.Mnemonic;
import com.glodnet.chain.exception.KeyException;
import com.glodnet.chain.keys.IKeyDAO;
import com.glodnet.chain.util.Bip39Utils;
import com.glodnet.chain.util.SM2Utils;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import cosmos.crypto.sm2.Keys;
import cosmos.tx.signing.v1beta1.Signing;
import cosmos.tx.v1beta1.TxOuterClass;
import org.apache.commons.lang3.ArrayUtils;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.DeterministicKey;
import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import org.mindrot.jbcrypt.BCrypt;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Hashtable;

import static com.glodnet.chain.keys.impl.BCryptImpl.decode_base64;
import static com.glodnet.chain.keys.impl.BCryptImpl.encode_base64;

public class SM2KeyServiceImpl extends AbstractKeyServiceImpl {

  private static final int LOG_ROUNDS = 12;
  private static final int REAL_SALT_BEGIN_POS = 7;
  private static final int REAL_SALT_BASE64_LEN = 22;
  private static final String PREFIX_SALT = "$2a$12$";
  private static final String ALGO_TYPE = "sm2";
  private static final String PRIV_KEY_NAME = "cosmos/PrivKeySm2";

  public SM2KeyServiceImpl(IKeyDAO keyDAO) {
    super(keyDAO);
  }

  @Override
  public Mnemonic addKey(String name, String password) throws KeyException {
    String mnemonic = Bip39Utils.generateMnemonic();
    DeterministicKey dk = super.generateDeterministicKey(mnemonic);

    ECPoint pubkey = SM2Utils.getPublicKeyFromPrivkey(dk.getPrivKey());

    byte[] encoded = pubkey.getEncoded(true);
    byte[] hash = Hash.sha256(encoded);
    byte[] pre20 = new byte[20];
    System.arraycopy(hash, 0, pre20, 0, 20);
    String addr = super.toBech32(pre20);
    super.saveKey(name, password, addr, dk.getPrivKeyBytes());
    return new Mnemonic(addr, mnemonic);
  }

  @Override
  public String recoverKey(String name, String password, String mnemonic, boolean derive, int index,
                           String saltPassword) throws KeyException {
    DeterministicKey dk = super.generateDeterministicKey(mnemonic);
    ECPoint pubkey = SM2Utils.getPublicKeyFromPrivkey(dk.getPrivKey());

    byte[] encoded = pubkey.getEncoded(true);
    byte[] hash = Hash.sha256(encoded);
    byte[] pre20 = new byte[20];
    System.arraycopy(hash, 0, pre20, 0, 20);
    String addr = super.toBech32(pre20);
    super.saveKey(name, password, addr, dk.getPrivKeyBytes());
    return addr;
  }
  @Override
  public String importFromKeystore(String name, String keyPassword, String keystorePassword, String keystore) throws KeyException, IOException {

    InputStream inputStream = new FileInputStream(keystore);
    ArmoredInputStream aIS = new ArmoredInputStream(inputStream);
    String[] headers = aIS.getArmorHeaders();
    Hashtable<String,String> headersTable = new Hashtable<>();
    for (String headersItem : headers){
      String[] itemSplit = headersItem.split(": ");
      headersTable.put(itemSplit[0], itemSplit[1]);
    }
    byte[] encBytes = new byte[77];
    aIS.read(encBytes);

    byte[] realSaltByte = Hex.decode(headersTable.get("salt"));
    String realSaltString = encode_base64(realSaltByte, 16);
    String salt = PREFIX_SALT + realSaltString;

    String keyHash = BCrypt.hashpw(keystorePassword, salt);
    byte[] keyHashByte = keyHash.getBytes(StandardCharsets.UTF_8);
    byte[] keyHashSha256 = Hash.sha256(keyHashByte);

    SimpleBox box = new SimpleBox(keyHashSha256);
    byte[] privKeyAmino = box.open(encBytes).get();
    byte[] privKeyTemp= Arrays.copyOfRange(privKeyAmino, 5, privKeyAmino.length);

    BigInteger privKey = new BigInteger(1,privKeyTemp);
    ECPoint pubkey = SM2Utils.getPublicKeyFromPrivkey(privKey);

    byte[] encoded = pubkey.getEncoded(true);
    byte[] hash = Hash.sha256(encoded);
    byte[] pre20 = new byte[20];
    System.arraycopy(hash, 0, pre20, 0, 20);
    String addr = super.toBech32(pre20);
    super.saveKey(name, keyPassword, addr, Utils.bigIntegerToBytes(privKey, 32));
    return addr;
  }

  @Override
  public String exportKeystore(String name, String keyPassword, String keystorePassword, File destinationDirectory) throws KeyException, IOException {
    Key key = super.getKey(name, keyPassword);
    byte[] privKeyTemp = key.getPrivKey();
    byte[] prefixAmino = getPrefixAmino(PRIV_KEY_NAME);
    byte[] privKeyAmino = ArrayUtils.addAll(prefixAmino,privKeyTemp);

    String salt = BCrypt.gensalt(LOG_ROUNDS);
    String keyHash = BCrypt.hashpw(keystorePassword, salt);
    byte[] keyHashByte = keyHash.getBytes(StandardCharsets.UTF_8);
    byte[] keyHashSha256 = Hash.sha256(keyHashByte);

    SimpleBox box = new SimpleBox(keyHashSha256);
    byte[] encBytes = box.seal(privKeyAmino);

    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    ArmoredOutputStreamImpl aOS = new ArmoredOutputStreamImpl(byteStream);
    String realSaltString = salt.substring(REAL_SALT_BEGIN_POS, REAL_SALT_BEGIN_POS+REAL_SALT_BASE64_LEN);
    byte[] realSaltByte = decode_base64(realSaltString, 16);
    aOS.setHeader("salt", Hex.toHexString(realSaltByte).toUpperCase());
    aOS.setHeader("kdf", "bcrypt");
    aOS.setHeader("type", ALGO_TYPE);
    aOS.write(encBytes);
    aOS.close();
    return writeArmorToFile(destinationDirectory, key.getAddress(), byteStream.toString().trim());
  }

  @Override
  public byte[] sign(String name, String keyPassword, byte[] signDoc) throws KeyException, CryptoException {
    Key key = super.getKey(name, keyPassword);
    BigInteger privKey = new BigInteger(1, key.getPrivKey());
    byte[] signature = SM2Utils.sign(privKey, signDoc);
    return  signature;
  }

  @Override
  public TxOuterClass.Tx signTx(TxOuterClass.Tx tx, String name, String password, String chainID,  Long accountNumber, Long sequence, boolean overwriteSig) throws KeyException, IOException, CryptoException {
    TxOuterClass.Tx.Builder txBuilder = TxOuterClass.Tx.newBuilder(tx);

    Key key = this.getKey(name, password);

    BigInteger privKey = new BigInteger(1, key.getPrivKey());
    ECPoint pubkey = SM2Utils.getPublicKeyFromPrivkey(privKey);
    byte[] encodedPubkey = pubkey.getEncoded(true);

    TxOuterClass.AuthInfo ai = TxOuterClass.AuthInfo.newBuilder()
            .addSignerInfos(
                    TxOuterClass.SignerInfo.newBuilder()
                            .setPublicKey(Any.pack(Keys.PubKey.newBuilder().setKey(ByteString.copyFrom(encodedPubkey)).build(), "/"))
                            .setModeInfo(TxOuterClass.ModeInfo.newBuilder().setSingle(TxOuterClass.ModeInfo.Single.newBuilder().setMode(Signing.SignMode.SIGN_MODE_DIRECT)))
                            .setSequence(sequence))
            .setFee(txBuilder.getAuthInfo().getFee()).build();

    TxOuterClass.SignDoc signdoc = TxOuterClass.SignDoc.newBuilder()
            .setBodyBytes(txBuilder.getBody().toByteString())
            .setAuthInfoBytes(ai.toByteString())
            .setAccountNumber(accountNumber)
            .setChainId(chainID)
            .build();

    byte[] signature = SM2Utils.sign(privKey, signdoc.toByteArray());
    BigInteger[] rs = SM2Utils.getRSFromSignature(signature);
    byte[] sigBytes = ArrayUtils.addAll(Numeric.toBytesPadded(rs[0], 32), Numeric.toBytesPadded(rs[1], 32));


    if (overwriteSig) {
      txBuilder.clearSignatures();
      txBuilder.setAuthInfo(ai);
    }

    return txBuilder.addSignatures(ByteString.copyFrom(sigBytes))
            .build();
  }
}
