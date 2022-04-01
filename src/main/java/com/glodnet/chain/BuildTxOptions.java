package com.glodnet.chain;

import com.google.protobuf.Any;
import cosmos.base.v1beta1.CoinOuterClass;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BuildTxOptions {
    private List<Any> msgs;
    private String memo;

    // Optional parameters
    @Builder.Default
    private String sender = "";
    @Builder.Default
    private Long accountNumber = 0L;
    @Builder.Default
    private Long sequence = 0L;
    @Builder.Default
    private Long gasLimit = 0L;
    @Builder.Default
    private CoinOuterClass.Coin feeAmount = null;
    @Builder.Default
    private String feeGranter = "";
    @Builder.Default
    private Long timeoutHeight = 0L;
}
