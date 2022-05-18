package com.glodnet.chain;

import com.glodnet.chain.util.AddressUtils;
import com.glodnet.chain.util.Bech32Utils;
import com.google.protobuf.Any;
import com.google.protobuf.Message;
import cosmos.staking.v1beta1.Staking.*;
import cosmos.staking.v1beta1.Tx.*;
import cosmos.base.v1beta1.CoinOuterClass.Coin;

import java.util.List;

public class TxStaking {
    /**
     * 创建消息结构--- 创建验证者
     *
     * @param delegator_address  委托人地址
     * @param amount 委托金额
     * @param moniker 名称
     * @param website 网站地址
     * @return MsgCreateValidator 对象
     */
    public static Any NewMsgCreateValidator(String delegator_address, Message pubKey, Coin amount, String moniker, String website) throws Exception {
        MsgCreateValidator msg = MsgCreateValidator.newBuilder()
                .setDescription(
                        Description.newBuilder()
                                .setMoniker(moniker)
                                .setWebsite(website)
                                .build()
                )
                .setCommission(
                        CommissionRates.newBuilder()
                                .setRate("100000000000000000")
                                .setMaxRate("200000000000000000")
                                .setMaxChangeRate("10000000000000000")
                                .build()
                )
                .setMinSelfDelegation("1")
                .setDelegatorAddress(delegator_address)
                .setValidatorAddress(Bech32Utils.valoperBech32(delegator_address))
                .setPubkey(Any.pack(pubKey, "/"))
                .setValue(amount)
                .build();
        return Any.pack(msg, "/");
    }

    /**
     * 创建消息结构--- 修改验证者
     *
     * @param validator_address  被委托人地址
     * @param moniker 名称
     * @param website 网站地址
     * @return MsgEditValidator 对象
     */
    public static Any NewMsgEditValidator(String validator_address, String moniker, String website) throws Exception {
        MsgEditValidator msg = MsgEditValidator.newBuilder()
                .setValidatorAddress(validator_address)
                .setDescription(
                        Description.newBuilder()
                        .setMoniker(moniker)
                        .setWebsite(website)
                        .build()
                )
                .build();
        return Any.pack(msg, "/");
    }

    /**
     * 创建消息结构--- 取消委托权益
     *
     * @param delegator_address  委托人地址
     * @param validator_address  被委托人地址
     * @param amount 委托金额
     * @return MsgDelegate 对象
     */
    public static Any NewMsgDelegate(String delegator_address, String validator_address, Coin amount) throws Exception {
        MsgDelegate msg = MsgDelegate.newBuilder()
                .setDelegatorAddress(delegator_address)
                .setValidatorAddress(validator_address)
                .setAmount(amount)
                .build();
        return Any.pack(msg, "/");
    }

    /**
     * 创建消息结构--- 变更被委托人
     *
     * @param delegator_address  委托人地址
     * @param validator_src_address 被委托人地址
     * @param validator_dst_address 被委托人地址
     * @param amount 委托金额
     * @return MsgBeginRedelegate 对象
     */
    public static Any NewMsgRedelegate(String delegator_address, String validator_src_address, String validator_dst_address, Coin amount) throws Exception {
        MsgBeginRedelegate msg = MsgBeginRedelegate.newBuilder()
                .setDelegatorAddress(delegator_address)
                .setValidatorSrcAddress(validator_src_address)
                .setValidatorDstAddress(validator_dst_address)
                .build();
        return Any.pack(msg, "/");
    }

    /**
     * 创建消息结构--- 取消委托权益
     *
     * @param delegator_address  委托人地址
     * @param amount 委托金额
     * @return NewMsgUndelegate 对象
     */
    public static Any NewMsgUndelegate(String delegator_address, Coin amount) throws Exception {
        MsgUndelegate msg = MsgUndelegate.newBuilder()
                .setDelegatorAddress(delegator_address)
                .setValidatorAddress(Bech32Utils.valoperBech32(delegator_address))
                .setAmount(amount)
                .build();
        return Any.pack(msg, "/");
    }
}
