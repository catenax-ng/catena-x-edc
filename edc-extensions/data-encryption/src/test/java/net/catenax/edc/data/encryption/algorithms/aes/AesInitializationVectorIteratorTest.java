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

import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import net.catenax.edc.data.encryption.util.ArrayUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AesInitializationVectorIteratorTest {

  @Test
  @SneakyThrows
  public void testDistinctVectors() {
    final int vectorCount = 100;
    AesInitializationVectorIterator iterator = new AesInitializationVectorIterator();
    iterator.initialize();

    List<byte[]> vectors = new ArrayList<>();
    for (var i = 0; i < vectorCount; i++) {
      vectors.add(iterator.next());
    }

    long distinctVectors = vectors.stream().map(ArrayUtil::byteArrayToHex).distinct().count();
    Assertions.assertEquals(vectorCount, distinctVectors);
  }

  @Test
  @SneakyThrows
  public void testHasNextTrueOnCounterContinuing() {
    ByteCounter counter = Mockito.mock(ByteCounter.class);
    AesInitializationVectorIterator iterator = new AesInitializationVectorIterator(counter);

    Mockito.when(counter.isMaxed()).thenReturn(false);
    Assertions.assertTrue(iterator.hasNext());
  }

  @Test
  @SneakyThrows
  public void testHasNextFalseOnCounterEnd() {
    ByteCounter counter = Mockito.mock(ByteCounter.class);
    AesInitializationVectorIterator iterator = new AesInitializationVectorIterator(counter);

    Mockito.when(counter.isMaxed()).thenReturn(true);
    Assertions.assertFalse(iterator.hasNext());
  }
}
