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

import static java.util.Objects.nonNull;

import org.eclipse.edc.connector.api.management.configuration.ManagementApiConfiguration;
import org.eclipse.edc.connector.contract.spi.negotiation.observe.ContractNegotiationListener;
import org.eclipse.edc.connector.contract.spi.negotiation.observe.ContractNegotiationObservable;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.spi.catalog.CatalogService;
import org.eclipse.edc.connector.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.spi.contractnegotiation.ContractNegotiationService;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.edr.EndpointDataReferenceReceiver;
import org.eclipse.edc.connector.transfer.spi.edr.EndpointDataReferenceReceiverRegistry;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.tractusx.edc.cp.adapter.messaging.Channel;
import org.eclipse.tractusx.edc.cp.adapter.messaging.InMemoryMessageBus;
import org.eclipse.tractusx.edc.cp.adapter.messaging.ListenerService;
import org.eclipse.tractusx.edc.cp.adapter.process.contractnegotiation.CatalogCachedRetriever;
import org.eclipse.tractusx.edc.cp.adapter.process.contractnegotiation.CatalogRetriever;
import org.eclipse.tractusx.edc.cp.adapter.process.contractnegotiation.ContractAgreementRetriever;
import org.eclipse.tractusx.edc.cp.adapter.process.contractnegotiation.ContractNegotiationHandler;
import org.eclipse.tractusx.edc.cp.adapter.process.contractnotification.*;
import org.eclipse.tractusx.edc.cp.adapter.process.datareference.DataRefInMemorySyncService;
import org.eclipse.tractusx.edc.cp.adapter.process.datareference.DataRefNotificationSyncService;
import org.eclipse.tractusx.edc.cp.adapter.process.datareference.DataReferenceHandler;
import org.eclipse.tractusx.edc.cp.adapter.process.datareference.EndpointDataReferenceReceiverImpl;
import org.eclipse.tractusx.edc.cp.adapter.service.ErrorResultService;
import org.eclipse.tractusx.edc.cp.adapter.service.ResultService;
import org.eclipse.tractusx.edc.cp.adapter.util.ExpiringMap;
import org.eclipse.tractusx.edc.cp.adapter.util.LockMap;

public class ApiAdapterExtension implements ServiceExtension {
  @Inject private Monitor monitor;
  @Inject private ContractNegotiationObservable negotiationObservable;
  @Inject private WebService webService;
  @Inject private ContractNegotiationService contractNegotiationService;
  @Inject private RemoteMessageDispatcherRegistry dispatcher;
  @Inject private EndpointDataReferenceReceiverRegistry receiverRegistry;
  @Inject private ManagementApiConfiguration apiConfig;
  @Inject private TransferProcessService transferProcessService;
  @Inject private ContractNegotiationStore contractNegotiationStore;
  @Inject private TransactionContext transactionContext;
  @Inject private CatalogService catalogService;
  @Inject private ContractAgreementService agreementService;

  @Override
  public String name() {
    return "Control Plane Adapter Extension";
  }

  @Override
  public void initialize(ServiceExtensionContext context) {
    ApiAdapterConfig config = new ApiAdapterConfig(context);
    ListenerService listenerService = new ListenerService();
    InMemoryMessageBus messageBus =
        new InMemoryMessageBus(
            monitor, listenerService, config.getInMemoryMessageBusThreadNumber());

    ResultService resultService = new ResultService(config.getDefaultSyncRequestTimeout());
    ErrorResultService errorResultService = new ErrorResultService(monitor, messageBus);
    ContractNotificationSyncService contractSyncService =
        new ContractInMemorySyncService(new LockMap());
    DataTransferInitializer dataTransferInitializer =
        new DataTransferInitializer(monitor, transferProcessService);
    ContractNotificationHandler contractNotificationHandler =
        new ContractNotificationHandler(
            monitor,
            messageBus,
            contractSyncService,
            contractNegotiationService,
            dataTransferInitializer);
    ContractNegotiationHandler contractNegotiationHandler =
        getContractNegotiationHandler(monitor, contractNegotiationService, messageBus, config);
    DataRefNotificationSyncService dataRefSyncService =
        new DataRefInMemorySyncService(new LockMap());
    DataReferenceHandler dataReferenceHandler =
        new DataReferenceHandler(monitor, messageBus, dataRefSyncService);

    listenerService.addListener(Channel.INITIAL, contractNegotiationHandler);
    listenerService.addListener(Channel.CONTRACT_CONFIRMATION, contractNotificationHandler);
    listenerService.addListener(Channel.DATA_REFERENCE, dataReferenceHandler);
    listenerService.addListener(Channel.RESULT, resultService);
    listenerService.addListener(Channel.DLQ, errorResultService);

    initHttpController(monitor, messageBus, resultService, config);
    initContractNegotiationListener(
        monitor, negotiationObservable, messageBus, contractSyncService, dataTransferInitializer);
    initDataReferenceReceiver(monitor, messageBus, dataRefSyncService);
  }

  private void initHttpController(
      Monitor monitor,
      InMemoryMessageBus messageBus,
      ResultService resultService,
      ApiAdapterConfig config) {
    webService.registerResource(
        apiConfig.getContextAlias(),
        new HttpController(monitor, resultService, messageBus, config));
  }

  private ContractNegotiationHandler getContractNegotiationHandler(
      Monitor monitor,
      ContractNegotiationService contractNegotiationService,
      InMemoryMessageBus messageBus,
      ApiAdapterConfig config) {
    return new ContractNegotiationHandler(
        monitor,
        messageBus,
        contractNegotiationService,
        new CatalogCachedRetriever(
            new CatalogRetriever(config.getCatalogRequestLimit(), catalogService),
            new ExpiringMap<>()),
        new ContractAgreementRetriever(monitor, agreementService));
  }

  private void initDataReferenceReceiver(
      Monitor monitor,
      InMemoryMessageBus messageBus,
      DataRefNotificationSyncService dataRefSyncService) {
    EndpointDataReferenceReceiver dataReferenceReceiver =
        new EndpointDataReferenceReceiverImpl(monitor, messageBus, dataRefSyncService);
    receiverRegistry.registerReceiver(dataReferenceReceiver);
  }

  private void initContractNegotiationListener(
      Monitor monitor,
      ContractNegotiationObservable negotiationObservable,
      InMemoryMessageBus messageBus,
      ContractNotificationSyncService contractSyncService,
      DataTransferInitializer dataTransferInitializer) {
    ContractNegotiationListener contractNegotiationListener =
        new ContractNegotiationListenerImpl(
            monitor, messageBus, contractSyncService, dataTransferInitializer);
    if (nonNull(negotiationObservable)) {
      negotiationObservable.registerListener(contractNegotiationListener);
    }
  }
}
