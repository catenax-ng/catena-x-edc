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
package org.eclipse.tractusx.edc.oauth2.jwt.decorator;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ExpJwtDecoratorTest {

  @Test
  void decorate() {
    final Clock clock = Mockito.mock(Clock.class);
    final Duration expiration = Duration.ofSeconds(100);

    final ExpJwtDecorator decorator = new ExpJwtDecorator(clock, expiration);

    Mockito.when(clock.instant()).thenReturn(Instant.ofEpochSecond(0));

    Assertions.assertTrue(decorator.claims().containsKey(JWTClaimNames.EXPIRATION_TIME));
    Assertions.assertEquals(
        new Date(100000), decorator.claims().get(JWTClaimNames.EXPIRATION_TIME));
  }

  @Test
  void constructorNull() {
    Assertions.assertThrows(NullPointerException.class, () -> new ExpJwtDecorator(null, null));
  }
}
