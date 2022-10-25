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

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Date;
import java.util.Map;
import lombok.SneakyThrows;
import org.eclipse.dataspaceconnector.spi.iam.ClaimToken;
import org.eclipse.dataspaceconnector.spi.result.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ExpValidationRuleTest {

  private static final String EXP = "exp";

  private ExpValidationRule rule;

  // Mocks
  private Clock clock;

  @BeforeEach
  void setUp() {
    clock = Mockito.mock(Clock.class);
    rule = new ExpValidationRule(clock);
  }

  @Test
  @SneakyThrows
  void checkNotExpired() {
    Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-01");
    final Map<String, Object> claims = Map.of(EXP, date);
    final ClaimToken token = ClaimToken.Builder.newInstance().claims(claims).build();

    Mockito.when(clock.instant()).thenReturn(date.toInstant().minusSeconds(30));
    final Result<Void> result = rule.checkRule(token, null);

    Assertions.assertTrue(result.succeeded());
  }

  @Test
  @SneakyThrows
  void checkExpired() {
    Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-01");
    final Map<String, Object> claims = Map.of(EXP, date);
    final ClaimToken token = ClaimToken.Builder.newInstance().claims(claims).build();

    Mockito.when(clock.instant()).thenReturn(date.toInstant().plusSeconds(30));
    final Result<Void> result = rule.checkRule(token, null);

    Assertions.assertFalse(result.succeeded());
  }
}
