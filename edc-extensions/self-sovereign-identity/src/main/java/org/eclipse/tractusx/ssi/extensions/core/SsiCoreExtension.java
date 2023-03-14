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
package org.eclipse.tractusx.ssi.extensions.core;

import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.tractusx.ssi.extensions.core.resolver.did.DidDocumentResolverRegistryImpl;
import org.eclipse.tractusx.ssi.extensions.core.resolver.key.SigningMethod;
import org.eclipse.tractusx.ssi.extensions.core.wallet.VerifiableCredentialWalletRegistryImpl;
import org.eclipse.tractusx.ssi.spi.did.resolver.DidDocumentResolverRegistry;
import org.eclipse.tractusx.ssi.spi.wallet.VerifiableCredentialWalletRegistry;

@Extension(value = SsiCoreExtension.EXTENSION_NAME)
@Provides({VerifiableCredentialWalletRegistry.class, DidDocumentResolverRegistry.class})
public class SsiCoreExtension implements ServiceExtension {
  public static final String EXTENSION_NAME = "SSI Core Extension";

  public static final String SETTINGS_WALLET = "edc.ssi.wallet";
  public static final String SETTING_DID_CONNECTOR = "edc.ssi.did.connector";
  public static final String SETTING_DID_OPERATOR = "edc.ssi.did.operator";
  public static final String SETTING_VERIFIABLE_PRESENTATION_SIGNING_METHOD =
      "edc.ssi.verifiable.presentation.signing.method";
  public static final String SETTING_VERIFIABLE_PRESENTATION_SIGNING_KEY_ALIAS =
      "edc.ssi.verifiable.presentation.signing.key.alias";
  public static final String SETTING_WALLET_STORAGE_MEMBERSHIP_CREDENTIAL_ALIAS =
      "edc.ssi.wallet.storage.membership.credential.alias";
  public static final String SETTING_WALLET_STORAGE_CREDENTIAL_ALIAS_LIST =
      "edc.ssi.wallet.storage.credential.alias.list";

  public static final String SETTING_VERIFIABLE_PRESENTATION_SIGNING_METHOD_DEFAULT =
      SigningMethod.SIGNING_METHOD_ES256;
  public static final String SETTING_DID_DEFAULT = "did:null:connector";

  @Override
  public String name() {
    return EXTENSION_NAME;
  }

  @Override
  public void start() {
    // TODO Check whether configured wallet was registered during initialize phase
    // TODO Check whether verifiable presentation signing key is supported / valid
    // TODO Check if credentials from settings are in the wallet
  }

  @Override
  public void initialize(ServiceExtensionContext context) {
    final Monitor monitor = context.getMonitor();

    final DidDocumentResolverRegistry documentResolverRegistry =
        new DidDocumentResolverRegistryImpl();

    final VerifiableCredentialWalletRegistry walletRegistry =
        new VerifiableCredentialWalletRegistryImpl();

    context.registerService(DidDocumentResolverRegistry.class, documentResolverRegistry);
    context.registerService(VerifiableCredentialWalletRegistry.class, walletRegistry);
  }
}
