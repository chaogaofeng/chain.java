package com.glodnet.chain;

import cn.hutool.core.io.FileUtil;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import cosmos.base.v1beta1.CoinOuterClass.Coin;
import cosmwasm.wasm.v1.Tx.*;
import cosmwasm.wasm.v1.Types.*;

import java.util.List;

public class TxWasm {
    /**
     * 创建消息结构---上传合约
     *
     * @param sender        用户地址
     * @param wasmFile      编译文件 *.wasm
     * @param instantiateByAddress  实例化用户地址, 为空任何用户可实例化
     * @return MsgStoreCode对象
     */
    public static Any NewMsgStoreCode(String sender, String wasmFile, String instantiateByAddress) throws Exception {
        AccessConfig.Builder accessConfig = AccessConfig.newBuilder();
        if (instantiateByAddress.length() == 0) {
            accessConfig.setPermission(AccessType.ACCESS_TYPE_EVERYBODY);
        } else {
            accessConfig.setPermission(AccessType.ACCESS_TYPE_ONLY_ADDRESS).setAddress(instantiateByAddress);
        }

        MsgStoreCode msg = MsgStoreCode.newBuilder()
                .setSender(sender)
                .setWasmByteCode(ByteString.copyFrom(FileUtil.readBytes(wasmFile)))
                .setInstantiatePermission(accessConfig)
                .build();
        return Any.pack(msg, "/");
    }

    /**
     *  创建消息结构--- 部署合约
     *
     * @param sender   用户地址
     * @param codeID   合约代码ID
     * @param initArgs 实例参数
     * @param label    标签
     * @param admin    管理员
     * @param amounts  金额
     * @return MsgInstantiateContract对象
     */
    public static Any NewMsgInstantiateContract(String sender, Long codeID, String initArgs, String label, String admin, List<Coin> amounts) {
        MsgInstantiateContract msg = MsgInstantiateContract.newBuilder()
                .setSender(sender)
                .setCodeId(codeID)
                .setMsg(ByteString.copyFromUtf8(initArgs))
                .setAdmin(admin)
                .setLabel(label)
                .addAllFunds(amounts)
                .build();
        return Any.pack(msg, "/");
    }

    /**
     *  创建消息结构--- 执行合约
     *
     * @param sender        用户地址
     * @param contractAddr  合约地址
     * @param execArgs      执行参数
     * @param amounts  金额
     * @return MsgExecuteContract对象
     */
    public static Any NewMsgExecuteContract(String sender, String contractAddr, String execArgs, List<Coin> amounts) {
        MsgExecuteContract msg = MsgExecuteContract.newBuilder()
                .setSender(sender)
                .setContract(contractAddr)
                .setMsg(ByteString.copyFromUtf8(execArgs))
                .addAllFunds(amounts)
                .build();
        return Any.pack(msg, "/");
    }

    /**
     *  创建消息结构--- 更新管理者
     *
     * @param sender        用户地址
     * @param contractAddr  合约地址
     * @param admin         管理者地址
     * @return MsgUpdateAdmin对象
     */
    public static Any NewMsgUpdateAdmin(String sender, String contractAddr, String admin) {
        MsgUpdateAdmin msg = MsgUpdateAdmin.newBuilder()
                .setSender(sender)
                .setContract(contractAddr)
                .setNewAdmin(admin)
                .build();
        return Any.pack(msg, "/");
    }

    /**
     *  创建消息结构--- 清空管理者
     *
     * @param sender        用户地址
     * @param contractAddr  合约地址
     * @return MsgUpdateAdmin对象
     */
    public static Any NewMsgClearAdmin(String sender, String contractAddr) {
        MsgClearAdmin msg = MsgClearAdmin.newBuilder()
                .setSender(sender)
                .setContract(contractAddr)
                .build();
        return Any.pack(msg, "/");
    }

    /**
     *  创建消息结构--- 升级合约
     *
     * @param sender        用户地址
     * @param contractAddr  合约地址
     * @param codeID   合约代码ID
     * @param migrateArgs 升级参数
     * @return MsgMigrateContract
     */
    public static Any NewMsgMigrateContract(String sender, String contractAddr, Long codeID, String migrateArgs) {
        MsgMigrateContract msg = MsgMigrateContract.newBuilder()
                .setSender(sender)
                .setContract(contractAddr)
                .setCodeId(codeID)
                .setMsg(ByteString.copyFromUtf8(migrateArgs))
                .build();
        return Any.pack(msg, "/");
    }
}
