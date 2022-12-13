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

  public static final String IDS = "ids";
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
    typeManager.registerContext(IDS, JsonLd.getObjectMapper());

    registerIdsClasses(typeManager);
    registerCustomConstraintImpl(typeManager);
  }

  private void registerIdsClasses(TypeManager typeManager) {
    registerSerializer(typeManager, ArtifactRequestMessage.class);
    registerSerializer(typeManager, RequestInProcessMessage.class);
    registerSerializer(typeManager, MessageProcessedNotificationMessage.class);
    registerSerializer(typeManager, DescriptionRequestMessage.class);
    registerSerializer(typeManager, NotificationMessage.class);
    registerSerializer(typeManager, ParticipantUpdateMessage.class);
    registerSerializer(typeManager, RejectionMessage.class);
    registerSerializer(typeManager, ContractAgreementMessage.class);
    registerSerializer(typeManager, ContractRejectionMessage.class);
    registerSerializer(typeManager, ContractOfferMessage.class);
    registerSerializer(typeManager, ContractRequestMessage.class);
    registerSerializer(typeManager, DynamicAttributeToken.class);
    registerSerializer(typeManager, TokenFormat.class);
    registerSerializer(typeManager, ContractAgreement.class);
    registerSerializer(typeManager, ContractOffer.class);
    registerSerializer(typeManager, Contract.class);
    registerSerializer(typeManager, Permission.class);
    registerSerializer(typeManager, Prohibition.class);
    registerSerializer(typeManager, Duty.class);
    registerSerializer(typeManager, Action.class);
    registerSerializer(typeManager, LogicalConstraint.class);
    registerSerializer(typeManager, Constraint.class);
    registerSerializer(typeManager, Artifact.class);
    registerSerializer(typeManager, BaseConnector.class);
    registerSerializer(typeManager, Representation.class);
    registerSerializer(typeManager, Resource.class);
    registerSerializer(typeManager, TypedLiteral.class);
    registerSerializer(typeManager, ResourceCatalog.class);
    registerSerializer(typeManager, CustomMediaType.class);
  }

  private void registerCustomConstraintImpl(TypeManager typeManager) {
    registerSerializer(typeManager, IdsConstraintImpl.class);
    typeManager.registerTypes(IDS, IdsConstraintImpl.class);
  }

  private <T> void registerSerializer(TypeManager typeManager, Class<T> clazz) {
    typeManager.registerSerializer(IDS, clazz, new JsonLdSerializer<>(clazz, IdsConstants.CONTEXT));
  }
}
