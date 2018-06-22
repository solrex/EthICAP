package com.ooolab;

import java.math.BigInteger;

public class EthICAP {
    private static String ICAP_XE_PREFIX = "XE";
    private static String IBAN_SCHEME = "iban://";
    private static String IBAN_MOD = "97";

    public static String buildICAP(String ethAddress) {
        if (!ethAddress.startsWith("0x") || ethAddress.length() != 42) {
            throw new IllegalArgumentException("Invalid ethereum address.");
        }
        BigInteger ethInt = new BigInteger(ethAddress.substring(2), 16);
        String base36Addr = ethInt.toString(36).toUpperCase();
        String checkAddr = base36Addr + ICAP_XE_PREFIX + "00";
        String base10Str = "";
        for (Character c:checkAddr.toCharArray()) {
            base10Str += new BigInteger(c.toString(), 36);
        }
        Integer checkSum = 98 - (new BigInteger(base10Str)).mod(new BigInteger(IBAN_MOD)).intValue();
        String icapAddress = IBAN_SCHEME + ICAP_XE_PREFIX + checkSum.toString() + base36Addr;
        return icapAddress;
    }

    public static String decodeICAP(String icapAddress) {
        if (!isValid(icapAddress)) {
            throw new IllegalArgumentException("Invalid icap address.");
        }
        BigInteger ethInt = new BigInteger(icapAddress.substring(11), 36);
        String base16Addr = ethInt.toString(16).toLowerCase();
        return "0x" + base16Addr;
    }

    public static boolean isValid(String icapAddress) {
        if (!icapAddress.startsWith("iban://XE") || icapAddress.length() != 42) {
            return false;
        }
        String base10Str = "";
        for (Character c:icapAddress.substring(11).toCharArray()) {
            base10Str += new BigInteger(c.toString(), 36);
        }
        for (Character c:icapAddress.substring(7, 11).toCharArray()) {
            base10Str += new BigInteger(c.toString(), 36);
        }
        Integer checkSum = (new BigInteger(base10Str)).mod(new BigInteger(IBAN_MOD)).intValue();
        return checkSum == 1;
    }
}
