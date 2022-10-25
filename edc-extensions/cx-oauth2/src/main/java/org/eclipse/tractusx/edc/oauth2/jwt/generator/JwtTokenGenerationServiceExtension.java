/*
 * Copyright (c) 2022 Mercedes-Benz Tech Innovation GmbH
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
package org.eclipse.tractusx.edc.oauth2.jwt.generator;

import java.security.PrivateKey;
import lombok.NonNull;
import lombok.Setter;
import org.eclipse.dataspaceconnector.runtime.metamodel.annotation.Inject;
import org.eclipse.dataspaceconnector.runtime.metamodel.annotation.Provides;
import org.eclipse.dataspaceconnector.runtime.metamodel.annotation.Requires;
import org.eclipse.dataspaceconnector.runtime.metamodel.annotation.Setting;
import org.eclipse.dataspaceconnector.spi.jwt.TokenGenerationService;
import org.eclipse.dataspaceconnector.spi.security.PrivateKeyResolver;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

@Provides(TokenGenerationService.class)
@Requires(PrivateKeyResolver.class)
public class JwtTokenGenerationServiceExtension implements ServiceExtension {

  @Setting private static final String PRIVATE_KEY_ALIAS = "edc.oauth.private.key.alias";

  @Inject @Setter private PrivateKeyResolver privateKeyResolver;

  @Override
  public void initialize(@NonNull final ServiceExtensionContext serviceExtensionContext) {
    final PrivateKey privateKey = privateKey(serviceExtensionContext);
    final TokenGenerationService tokenGenerationService = new JwtTokenGenerationService(privateKey);

    serviceExtensionContext.registerService(TokenGenerationService.class, tokenGenerationService);
  }

  private PrivateKey privateKey(final ServiceExtensionContext serviceExtensionContext) {
    final String privateKeyAlias = serviceExtensionContext.getConfig().getString(PRIVATE_KEY_ALIAS);
    return privateKeyResolver.resolvePrivateKey(privateKeyAlias, PrivateKey.class);
  }
}
