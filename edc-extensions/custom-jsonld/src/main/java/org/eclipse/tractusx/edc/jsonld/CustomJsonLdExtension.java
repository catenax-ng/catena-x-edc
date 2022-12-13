/*
 * Copyright (c) 2022 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2021,2022 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.eclipse.tractusx.edc.jsonld;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import org.eclipse.edc.protocol.ids.jsonld.JsonLd;
import org.eclipse.edc.protocol.ids.jsonld.JsonLdSerializer;
import org.eclipse.edc.protocol.ids.serialization.IdsConstraintImpl;
import org.eclipse.edc.protocol.ids.spi.domain.IdsConstants;
import org.eclipse.edc.protocol.ids.spi.service.ConnectorService;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;

/**
 * This extension is a temporary fix for a serialization bug:
 * https://github.com/eclipse-edc/Connector/issues/2334
 */
public class CustomJsonLdExtension implements ServiceExtension {

  // this is only needed to ensure that this extension is initialized after the
  // IdsCoreServiceExtension
  @Inject private ConnectorService connectorService;

  @Override
  public String name() {
    return "Custom JsonLD";
  }

  @Override
  public void initialize(ServiceExtensionContext context) {
    var typeManager = context.getTypeManager();
    typeManager.registerContext("ids", JsonLd.getObjectMapper());

    registerIdsClasses(typeManager);
    registerCustomConstraintImpl(typeManager);
  }

  private void registerIdsClasses(TypeManager typeManager) {
    typeManager.registerSerializer(
        "ids",
        ArtifactRequestMessage.class,
        new JsonLdSerializer<>(ArtifactRequestMessage.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids",
        RequestInProcessMessage.class,
        new JsonLdSerializer<>(RequestInProcessMessage.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids",
        MessageProcessedNotificationMessage.class,
        new JsonLdSerializer<>(MessageProcessedNotificationMessage.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids",
        DescriptionRequestMessage.class,
        new JsonLdSerializer<>(DescriptionRequestMessage.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids",
        NotificationMessage.class,
        new JsonLdSerializer<>(NotificationMessage.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids",
        ParticipantUpdateMessage.class,
        new JsonLdSerializer<>(ParticipantUpdateMessage.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids",
        RejectionMessage.class,
        new JsonLdSerializer<>(RejectionMessage.class, IdsConstants.CONTEXT));

    typeManager.registerSerializer(
        "ids",
        ContractAgreementMessage.class,
        new JsonLdSerializer<>(ContractAgreementMessage.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids",
        ContractRejectionMessage.class,
        new JsonLdSerializer<>(ContractRejectionMessage.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids",
        ContractOfferMessage.class,
        new JsonLdSerializer<>(ContractOfferMessage.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids",
        ContractRequestMessage.class,
        new JsonLdSerializer<>(ContractRequestMessage.class, IdsConstants.CONTEXT));

    typeManager.registerSerializer(
        "ids",
        DynamicAttributeToken.class,
        new JsonLdSerializer<>(DynamicAttributeToken.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids", TokenFormat.class, new JsonLdSerializer<>(TokenFormat.class, IdsConstants.CONTEXT));

    typeManager.registerSerializer(
        "ids",
        ContractAgreement.class,
        new JsonLdSerializer<>(ContractAgreement.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids",
        ContractOffer.class,
        new JsonLdSerializer<>(ContractOffer.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids", Contract.class, new JsonLdSerializer<>(Contract.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids", Permission.class, new JsonLdSerializer<>(Permission.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids", Prohibition.class, new JsonLdSerializer<>(Prohibition.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids", Duty.class, new JsonLdSerializer<>(Duty.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids", Action.class, new JsonLdSerializer<>(Action.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids",
        LogicalConstraint.class,
        new JsonLdSerializer<>(LogicalConstraint.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids", Constraint.class, new JsonLdSerializer<>(Constraint.class, IdsConstants.CONTEXT));

    typeManager.registerSerializer(
        "ids", Artifact.class, new JsonLdSerializer<>(Artifact.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids",
        BaseConnector.class,
        new JsonLdSerializer<>(BaseConnector.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids",
        Representation.class,
        new JsonLdSerializer<>(Representation.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids", Resource.class, new JsonLdSerializer<>(Resource.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids",
        TypedLiteral.class,
        new JsonLdSerializer<>(TypedLiteral.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids",
        ResourceCatalog.class,
        new JsonLdSerializer<>(ResourceCatalog.class, IdsConstants.CONTEXT));
    typeManager.registerSerializer(
        "ids",
        CustomMediaType.class,
        new JsonLdSerializer<>(CustomMediaType.class, IdsConstants.CONTEXT));
  }

  private void registerCustomConstraintImpl(TypeManager typeManager) {
    typeManager.registerSerializer(
        "ids",
        IdsConstraintImpl.class,
        new JsonLdSerializer<>(IdsConstraintImpl.class, IdsConstants.CONTEXT));
    typeManager.registerTypes("ids", IdsConstraintImpl.class);
  }
}
