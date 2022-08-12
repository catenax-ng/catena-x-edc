/*
 *  Copyright (c) 2022 Mercedes-Benz Tech Innovation GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Mercedes-Benz Tech Innovation GmbH - Initial API and Implementation
 *
 */

package net.catenax.edc.data.encryption.encrypter;

import java.time.Duration;
import lombok.NonNull;
import lombok.Value;

@Value
public class DataEncrypterConfiguration {
  @NonNull
  String algorithm;
  @NonNull
  String keySetAlias;
  boolean cachingEnabled;
  Duration cachingDuration;
}
