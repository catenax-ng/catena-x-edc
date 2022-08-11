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

package net.catenax.edc.data.encryption;

import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DataEncryptionExtensionTest {

  private DataEncryptionExtension extension;

  // mocks
  private Monitor monitor;
  private ServiceExtensionContext context;

  @BeforeEach
  void setup() {
    monitor = Mockito.mock(Monitor.class);
    context = Mockito.mock(ServiceExtensionContext.class);

    extension = new DataEncryptionExtension();
  }

  @Test
  void testName() {
    Assertions.assertEquals(DataEncryptionExtension.NAME, extension.name());
  }
}
