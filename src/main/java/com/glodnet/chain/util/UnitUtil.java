package com.glodnet.chain.util;

import java.math.BigDecimal;
import java.math.BigInteger;

// 1 gnc = 1,000,000 ugnc
// 	gnc  = "gnc"  // 1 (base denom unit)
//	mgnc = "mgnc" // 10^-3 (milli)
//	ugnc = "ugnc" // 10^-6 (micro)
//	ngnc = "ngnc" // 10^-9 (nano)
public class UnitUtil {
    public static BigDecimal uGncToGnc(String ugncString) {
        BigDecimal ugnc = new BigDecimal(ugncString);
        return ugnc.movePointLeft(6).stripTrailingZeros();
    }

    public static BigDecimal uGncToGnc(BigInteger ugncBigInteger) {
        BigDecimal ugnc = new BigDecimal(ugncBigInteger);
        return ugnc.movePointLeft(6).stripTrailingZeros();
    }

    public static BigDecimal gncTouGnc(String gncVal) {
        BigDecimal gnc = new BigDecimal(gncVal);
        return gncTouGnc(gnc);
    }

    public static BigDecimal gncTouGnc(BigDecimal gnc) {
        return gnc.movePointRight(6).stripTrailingZeros();
    }

    public static BigInteger gncTouGncBigInteger(BigDecimal gnc) {
        BigDecimal bigDecimal = gnc.movePointRight(6);
        if (getNumberOfDecimalPlaces(bigDecimal) != 0) {
            throw new RuntimeException("gnc to uGnc: 转换成整数后，含有小数点:" + bigDecimal);
        }
        // 忽略小数位
        return bigDecimal.toBigInteger();
    }

    // 小数位位数
    public static int getNumberOfDecimalPlaces(BigDecimal bigDecimal) {
        String string = bigDecimal.stripTrailingZeros().toPlainString();
        int index = string.indexOf(".");
        return index < 0 ? 0 : string.length() - index - 1;
    }

}
