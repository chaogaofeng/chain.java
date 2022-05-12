package com.glodnet.chain;

import com.glodnet.chain.keys.IKeyDAO;
import com.glodnet.chain.keys.IKeyService;
import com.glodnet.chain.keys.impl.DefaultKeyServiceImpl;
import com.glodnet.chain.keys.impl.SM2KeyServiceImpl;
import com.glodnet.chain.util.JsonToProtoObjectUtil;
import com.glodnet.chain.util.SignAlgo;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.JsonFormat;
import cosmos.auth.v1beta1.Auth;
import cosmos.base.abci.v1beta1.Abci;
import cosmos.base.v1beta1.CoinOuterClass.*;
import cosmos.crypto.secp256k1.Keys;
import cosmos.tx.signing.v1beta1.Signing;
import cosmos.tx.v1beta1.ServiceOuterClass;
import cosmos.tx.v1beta1.TxOuterClass;
import io.netty.util.internal.StringUtil;

import java.math.BigDecimal;

public class Tx {
    private static final JsonFormat.Printer printer = JsonToProtoObjectUtil.getPrinter();

    // rest api conn
    private final HttpClient client;
    // node_info 的 network 字段
    private final String chainID;
    // gas 单价
    private final DecCoin gasPrice;
    // gas 调整系数
    private final BigDecimal gasAdjustment;

    private final SignAlgo signAlgo;
    private final IKeyDAO keyDAO;

    private IKeyService keyService;

    /**
     * 创建Tx对象
     *
     * @param chainID        网络ChainID。 例： gnchain
     * @param gasPrice       gas手续费。自动计算feeAmount时使用。 例： 0.00002ugnc
     * @param gasAdjustment  gas调整系数。自动计算gasLimit时使用。例： 1.1
     * @return Tx对象
     */
    public Tx(HttpClient client, IKeyDAO keyDAO, SignAlgo signAlgo, String chainID, DecCoin gasPrice, BigDecimal gasAdjustment) {
        this.keyDAO = keyDAO;
        this.signAlgo = signAlgo;
        this.client = client;
        this.chainID = chainID;
        this.gasPrice = gasPrice;
        this.gasAdjustment = gasAdjustment;
    }

    public IKeyService getKeyService() {
        if (this.keyService != null) {
            return this.keyService;
        }
        if (this.signAlgo == SignAlgo.SM2) {
            this.keyService = new SM2KeyServiceImpl(this.keyDAO);
        } else {
            this.keyService = new DefaultKeyServiceImpl(this.keyDAO);
        }
        return this.keyService;
    }

    /**
     * 构建交易
     */
    public TxOuterClass.Tx build(BuildTxOptions options) throws Exception {
        TxOuterClass.TxBody txBody = TxOuterClass.TxBody.newBuilder()
                .addAllMessages(options.getMsgs())
                .setMemo(options.getMemo())
                .setTimeoutHeight(options.getTimeoutHeight())
                .build();

        if (options.getAccountNumber() == 0 || options.getSequence() == 0) {
            Auth.BaseAccount baseAccount = QueryAuth.getBaseAccount(client, options.getSender());
            options.setAccountNumber(baseAccount.getAccountNumber());
            options.setSequence(baseAccount.getSequence());
        }

        if (options.getGasLimit() == 0) {
            TxOuterClass.ModeInfo.Single single = TxOuterClass.ModeInfo.Single.newBuilder()
                    .setMode(Signing.SignMode.SIGN_MODE_DIRECT)
                    .build();

            TxOuterClass.SignerInfo signerInfo = TxOuterClass.SignerInfo.newBuilder()
                    .setPublicKey(Any.pack(Keys.PubKey.newBuilder().build(), "/"))
                    .setModeInfo(TxOuterClass.ModeInfo.newBuilder().setSingle(single).build())
                    .setSequence(options.getSequence())
                    .build();

            TxOuterClass.Fee fee = TxOuterClass.Fee.newBuilder()
                    .setGasLimit(options.getGasLimit())
                    .build();

            TxOuterClass.Tx tx = TxOuterClass.Tx.newBuilder()
                    .setBody(txBody)
                    .setAuthInfo(TxOuterClass.AuthInfo.newBuilder()
                            .setFee(fee)
                            .addSignerInfos(signerInfo)
                            .build())
                    .addSignatures(ByteString.copyFrom(new byte[0]))
                    .build();

            ServiceOuterClass.SimulateResponse simulateResponse = Node.simulate(client, ServiceOuterClass.SimulateRequest.newBuilder().
                    setTxBytes(tx.toByteString())
                    .build());

            options.setGasLimit(gasAdjustment.multiply(new BigDecimal(simulateResponse.getGasInfo().getGasUsed())).longValue());
        }

        if (options.getFeeAmount() == null) {
            Long amount = new BigDecimal(gasPrice.getAmount()).multiply(new BigDecimal(options.getGasLimit())).longValue();
            options.setFeeAmount(Coin.newBuilder()
                    .setAmount(new BigDecimal(amount).toString())
                    .setDenom(gasPrice.getDenom())
                    .build());
        }

        TxOuterClass.Fee fee = TxOuterClass.Fee.newBuilder()
                .setGasLimit(options.getGasLimit())
                .setPayer(options.getFeeGranter())
                .addAmount(options.getFeeAmount())
                .build();

        TxOuterClass.AuthInfo authInfo = TxOuterClass.AuthInfo.newBuilder()
                .setFee(fee)
                .build();

        TxOuterClass.Tx.Builder txBuilder = TxOuterClass.Tx.newBuilder()
                .setAuthInfo(authInfo)
                .setBody(txBody);

        return txBuilder.build();
    }

    /**
     * 签名交易
     */
    public TxOuterClass.Tx sign(String name, String password, TxOuterClass.Tx tx, Long accountNumber, Long sequence, boolean overwriteSig) throws Exception {
        return getKeyService().signTx(tx, name, password, chainID, accountNumber, sequence, overwriteSig);
    }

    /**
     * 广播交易
     */
    public String broadcast(TxOuterClass.Tx tx) throws Exception {
        ServiceOuterClass.BroadcastTxResponse broadcastTxResponse = Node.broadcastTx(client, ServiceOuterClass.BroadcastTxRequest.newBuilder()
                .setTxBytes(tx.toByteString())
                .setMode(ServiceOuterClass.BroadcastMode.BROADCAST_MODE_SYNC)
                .build());

        if (!broadcastTxResponse.hasTxResponse()) {
            throw new Exception("broadcastTxResponse no body\n" + printer.print(tx));
        }

        Abci.TxResponse txResponse = broadcastTxResponse.getTxResponse();
        if (txResponse.getCode() != 0 || !StringUtil.isNullOrEmpty(txResponse.getCodespace())) {
            throw new Exception("BroadcastTx error:" + txResponse.getCodespace() + "," + txResponse.getCode() + "," + txResponse.getRawLog() + "\n" + printer.print(tx));
        }

        if (txResponse.getTxhash().length() != 64) {
            throw new Exception("Txhash illegal\n" + printer.print(tx));
        }

        return txResponse.getTxhash();
    }

    /**
     * 发送交易(构建、签名、广播）
     */
    public String send(String name, String password, BuildTxOptions options) throws Exception {
        return broadcast(sign(name, password, build(options), options.getAccountNumber(), options.getSequence(), true));
    }
}
