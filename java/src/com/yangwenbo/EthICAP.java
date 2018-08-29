/*
   Copyright 2018 Wenbo Yang <https://yangwenbo.com>

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.yangwenbo;

import java.math.BigInteger;

/**
 * Ethereum ICAP (Inter exchange Client Address Protocol) Address Converter
 * Convert Ethereum Address from/to ICAP iban address
 *
 * @ref https://github.com/ethereum/wiki/wiki/ICAP:-Inter-exchange-Client-Address-Protocol
 */
public class EthICAP {
    private static String ICAP_XE_PREFIX = "XE";
    private static String IBAN_SCHEME = "iban:";
    private static String IBAN_MOD = "97";

    /**
     * Build ICAP iban address from ethereum address.
     *
     * @param ethAddress ethereum address
     * @return ICAP iban address
     * @example
     * input:  0x730aea2b39aa2cf6b24829b3d39dc9a1f9297b88
     * return: iban:XE42DFRZLRUTFTFY4EVINAHYF7TQ6MACYH4
     */
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

    /**
     * Decode ethereum address from ICAP iban address
     *
     * @param icapAddress ICAP iban address
     * @return ethereum address
     * @example
     * input:  iban:XE42DFRZLRUTFTFY4EVINAHYF7TQ6MACYH4
     * return: 0x730aea2b39aa2cf6b24829b3d39dc9a1f9297b88
     */
    public static String decodeICAP(String icapAddress) {
        if (!isValid(icapAddress)) {
            throw new IllegalArgumentException("Invalid icap address.");
        }
        BigInteger ethInt = new BigInteger(icapAddress.substring(9), 36);
        String base16Addr = ethInt.toString(16).toLowerCase();
        return "0x" + base16Addr;
    }

    /**
     * Check ICAP iban address validation
     *
     * @param icapAddress ICAP iban address
     * @return true if valid; false if invalid
     */
    public static boolean isValid(String icapAddress) {
        if (!icapAddress.startsWith("iban:XE") || icapAddress.length() != 40) {
            return false;
        }
        String base10Str = "";
        for (Character c:icapAddress.substring(9).toCharArray()) {
            base10Str += new BigInteger(c.toString(), 36);
        }
        for (Character c:icapAddress.substring(5, 9).toCharArray()) {
            base10Str += new BigInteger(c.toString(), 36);
        }
        Integer checkSum = (new BigInteger(base10Str)).mod(new BigInteger(IBAN_MOD)).intValue();
        return checkSum == 1;
    }
}
