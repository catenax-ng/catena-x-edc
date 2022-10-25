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

import java.util.List;
import java.util.Map;
import org.eclipse.dataspaceconnector.spi.iam.ClaimToken;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.result.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AudValidationRuleTest {

  private static final String AUDIENCE = "audience";

  private AudValidationRule rule;

  @BeforeEach
  void setup() {
    final Monitor monitor = Mockito.mock(Monitor.class);
    rule = new AudValidationRule(AUDIENCE, monitor);
  }

  @Test
  void checkRuleSuccess() {
    final Map<String, Object> claims = Map.of("aud", List.of(AUDIENCE));
    final ClaimToken token = ClaimToken.Builder.newInstance().claims(claims).build();
    Result<Void> result = rule.checkRule(token, null);

    Assertions.assertTrue(result.succeeded());
  }

  @Test
  void checkRuleNoClaims() {
    final Map<String, Object> claims = Map.of();
    final ClaimToken token = ClaimToken.Builder.newInstance().claims(claims).build();
    Result<Void> result = rule.checkRule(token, null);

    Assertions.assertFalse(result.succeeded());
  }

  @Test
  void checkRuleClaimMissing() {
    final Map<String, Object> claims = Map.of("foo", List.of(AUDIENCE));
    final ClaimToken token = ClaimToken.Builder.newInstance().claims(claims).build();
    Result<Void> result = rule.checkRule(token, null);

    Assertions.assertFalse(result.succeeded());
  }

  @Test
  void checkRuleAudNotList() {
    final Map<String, Object> claims = Map.of("aud", AUDIENCE);
    final ClaimToken token = ClaimToken.Builder.newInstance().claims(claims).build();
    Result<Void> result = rule.checkRule(token, null);

    Assertions.assertFalse(result.succeeded());
  }
}
