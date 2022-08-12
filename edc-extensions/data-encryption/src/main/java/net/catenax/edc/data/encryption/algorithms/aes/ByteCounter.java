/*
*  Copyright (c) 2022 Mercedes-Benz Tech Innovation GmbH
*
*  This program and the accompanying materials are made available under the
*  terms of the Apache License, Version 2.0 which is available at
*  https://www.apache.org/licenses/LICENSE-2.0
*
*  SPDX-License-Identifier: Apache-2.0
*
*  Contributors:
*       Mercedes-Benz Tech Innovation GmbH - Initial API and Implementation
*
*/
package net.catenax.edc.data.encryption.algorithms.aes;

/**
 * Big Endian Byte Counter
 */
public class ByteCounter {

    private final byte[] counter;

    /**
     * Constructs a new ByteCounter with the given number of bytes.
     * E.g. a ByteCounter with 4 bytes will have a counter value of [0, 0, 0, 0].
     *
     * @param size number of bytes used by the counter
     */
    public ByteCounter(int size) {
        this.counter = new byte[size];
    }

    /**
     * Constructs a new ByteCounter with the given counter value.
     * Counter cannot grow bigger than the size of the array.
     *
     * @param counter initial counter value
     */
    public ByteCounter(byte[] counter) {
        this.counter = counter;
    }

    /**
     * Returns the counter value as a byte array.
     */
    public byte[] getBytes() {
        return counter;
    }

    /**
     * Returns true if counter is maxed
     */
    public boolean isMaxed() {
        for (byte b : counter) {
            if (b != (byte) 0xff)
                return false;
        }
        return true;
    }

    /**
     * Increments the counter by one.
     * 
     * @throws IllegalStateException if the counter is already maxed
     */
    public void increment() {
        incrementByte(counter.length - 1);
    }

    private void incrementByte(int index) {
        if (isMaxed()) {
            throw new IllegalStateException("Counter is already maxed");
        }

        if (counter[index] == (byte) 0xff) {
            counter[index] = (byte) 0x00;
            incrementByte(index - 1);
        } else {
            counter[index]++;
        }
    }

}
