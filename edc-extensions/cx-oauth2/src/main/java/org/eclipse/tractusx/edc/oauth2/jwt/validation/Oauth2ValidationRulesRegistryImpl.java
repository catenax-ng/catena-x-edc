/*
 *  Copyright (c) 2022 Amadeus
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
import lombok.NoArgsConstructor;
import org.eclipse.dataspaceconnector.iam.oauth2.spi.Oauth2ValidationRulesRegistry;
import org.eclipse.dataspaceconnector.spi.jwt.TokenValidationRule;
import org.eclipse.dataspaceconnector.spi.jwt.TokenValidationRulesRegistry;

/** Registry for Oauth2 validation rules. */
@NoArgsConstructor
public class Oauth2ValidationRulesRegistryImpl
    implements Oauth2ValidationRulesRegistry, TokenValidationRulesRegistry {

  private final List<TokenValidationRule> rules = new ArrayList<>();

  @Override
  public void addRule(TokenValidationRule rule) {
    rules.add(rule);
  }

  @Override
  public List<TokenValidationRule> getRules() {
    return new ArrayList<>(rules);
  }
}
