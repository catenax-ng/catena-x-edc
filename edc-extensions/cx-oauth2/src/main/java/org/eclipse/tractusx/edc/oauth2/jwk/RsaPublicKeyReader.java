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
package org.eclipse.tractusx.edc.oauth2.jwk;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;

@RequiredArgsConstructor
public class RsaPublicKeyReader implements PublicKeyReader {
  private static final String RSA = "RSA";
  private static final KeyFactory KEY_FACTORY;

  static {
    try {
      KEY_FACTORY = KeyFactory.getInstance(RSA);
    } catch (final NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new RuntimeException(noSuchAlgorithmException.getMessage(), noSuchAlgorithmException);
    }
  }

  @NonNull private final Monitor monitor;

  @Override
  public boolean canRead(final JsonWebKey jsonWebKey) {
    return Optional.ofNullable(jsonWebKey).map(JsonWebKey::getKty).stream()
        .allMatch(RSA::equalsIgnoreCase);
  }

  @Override
  public Optional<PublicKeyHolder> read(final JsonWebKey jsonWebKey) {
    try {
      final BigInteger modulus = unsignedInt(jsonWebKey.getNn());
      final BigInteger exponent = unsignedInt(jsonWebKey.getEe());
      final RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, exponent);
      final PublicKey publicKey = KEY_FACTORY.generatePublic(rsaPublicKeySpec);
      final PublicKeyHolder publicKeyHolder =
          PublicKeyHolder.builder().keyId(jsonWebKey.getKid()).publicKey(publicKey).build();

      return Optional.of(publicKeyHolder);
    } catch (final GeneralSecurityException generalSecurityException) {
      monitor.severe(
          "Error parsing identity provider public key, skipping. The kid is: "
              + jsonWebKey.getKid());
    }
    return Optional.empty();
  }

  private BigInteger unsignedInt(@NonNull final String value) {
    return new BigInteger(1, Base64.getUrlDecoder().decode(value));
  }
}
