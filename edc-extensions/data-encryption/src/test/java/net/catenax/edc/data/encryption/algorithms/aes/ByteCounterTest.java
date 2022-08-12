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

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class ByteCounterTest {

    @ParameterizedTest
    @ArgumentsSource(IncrementArgumentsProvider.class)
    public void testIncrements(byte[] counterValue, long numberOfIncrements, byte[] expected) {

        ByteCounter initializationVector = new ByteCounter(counterValue);

        for (int i = 0; i < numberOfIncrements; i++) {
            initializationVector.increment();
        }

        var result = initializationVector.getBytes();
        Assertions.assertEquals(byteArrayToHex(expected), byteArrayToHex(result));
    }

    @Test
    public void testIsMaxed() {

        byte[] counterValue = new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff };
        ByteCounter initializationVector = new ByteCounter(counterValue);

        Assertions.assertTrue(initializationVector.isMaxed());
    }

    @Test
    public void testOverflow() {

        byte[] counterValue = new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff };
        ByteCounter initializationVector = new ByteCounter(counterValue);

        Assertions.assertThrows(IllegalStateException.class, () -> initializationVector.increment());
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private static class IncrementArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                    Arguments.of(
                            new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 },
                            0, new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 }),
                    Arguments.of(new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 },
                            1, new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01 }),
                    Arguments.of(new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 }, 2,
                            new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02 }),
                    Arguments.of(new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 }, 255,
                            new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xff }),
                    Arguments.of(new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 }, 65535,
                            new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0xff, (byte) 0xff }),
                    Arguments.of(new byte[] { (byte) 0xef, (byte) 0xff, (byte) 0xff, (byte) 0xff }, 1,
                            new byte[] { (byte) 0xf0, (byte) 0x00, (byte) 0x00, (byte) 0x00 }));
        }
    }
}
