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
package org.eclipse.tractusx.edc.oauth2.jwt.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.dataspaceconnector.spi.iam.ClaimToken;
import org.eclipse.dataspaceconnector.spi.jwt.TokenValidationRule;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.result.Result;
import org.eclipse.tractusx.edc.oauth2.jwt.decorator.JWTClaimNames;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class AudValidationRule implements TokenValidationRule {
  @NonNull private final String audience;

  @NonNull private final Monitor monitor;

  /**
   * Validates the JWT by checking the audience, nbf, and expiration. Accessible for testing.
   *
   * @param toVerify The jwt including the claims.
   * @param additional No more additional information needed for this validation, can be null.
   */
  @Override
  @SneakyThrows
  public Result<Void> checkRule(
      @NotNull ClaimToken toVerify, @Nullable Map<String, Object> additional) {
    final List<String> errors = new ArrayList<>();

    final Object claim = toVerify.getClaims().get(JWTClaimNames.AUDIENCE);
    if (!(claim instanceof List)) {
      errors.add("Audience claim is not a list");
    } else {
      final List<?> audiences = (List<?>) claim;
      audiences.forEach(a -> monitor.info("RECEIVED DAP AUDIENCE TO VERIFY: " + a));

      if (audiences.isEmpty()) {
        errors.add("Required audience (aud) claim is missing in token");
      } else if (!audiences.contains(audience)) {
        errors.add("Token audience (aud) claim did not contain connector audience: " + audience);
      }
    }

    if (errors.isEmpty()) {
      return Result.success();
    } else {
      return Result.failure(errors);
    }
  }
}
