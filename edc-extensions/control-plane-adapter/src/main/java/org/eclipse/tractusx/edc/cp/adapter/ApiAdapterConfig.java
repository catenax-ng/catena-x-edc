/*
 * Copyright (c) 2022 ZF Friedrichshafen AG
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 * ZF Friedrichshafen AG - Initial API and Implementation
 *
 */

package org.eclipse.tractusx.edc.cp.adapter;

import org.eclipse.edc.spi.system.ServiceExtensionContext;

public class ApiAdapterConfig {
  private static final String DEFAULT_MESSAGE_RETRY_NUMBER =
      "edc.cp.adapter.default.message.retry.number";
  private static final String DEFAULT_SYNC_REQUEST_TIMEOUT =
      "edc.cp.adapter.default.sync.request.timeout";
  private static final String IN_MEMORY_MESSAGE_BUS_THREAD_NUMBER =
      "edc.cp.adapter.messagebus.inmemory.thread.number";
  private static final String CATALOG_EXPIRE_AFTER_TIME =
      "edc.cp.adapter.cache.catalog.expire.after";
  private static final String CATALOG_REQUEST_LIMIT = "edc.cp.adapter.catalog.request.limit";
  private static final String REUSE_CONTRACT_AGREEMENT = "edc.cp.adapter.reuse.contract.agreement";

  private final ServiceExtensionContext context;

  public ApiAdapterConfig(ServiceExtensionContext context) {
    this.context = context;
  }

  public int getDefaultMessageRetryNumber() {
    return context.getSetting(DEFAULT_MESSAGE_RETRY_NUMBER, 3);
  }

  public int getDefaultSyncRequestTimeout() {
    return context.getSetting(DEFAULT_SYNC_REQUEST_TIMEOUT, 20);
  }

  public int getInMemoryMessageBusThreadNumber() {
    return context.getSetting(IN_MEMORY_MESSAGE_BUS_THREAD_NUMBER, 10);
  }

  public boolean isContractAgreementReuseOn() {
    return context.getSetting(REUSE_CONTRACT_AGREEMENT, 1) != 0;
  }

  public int getCatalogExpireAfterTime() {
    return context.getSetting(CATALOG_EXPIRE_AFTER_TIME, 3600);
  }

  public int getCatalogRequestLimit() {
    return context.getSetting(CATALOG_REQUEST_LIMIT, 100);
  }
}
