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

package net.catenax.edc.hashicorpvault;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.Call;
import okhttp3.Callback;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
abstract class ResponseCallback<T> implements Callback {
  @NonNull protected final CompletableFuture<T> completableFuture;

  public void onFailure(@NotNull Call call, @NotNull IOException ioException) {
    completableFuture.completeExceptionally(
        new HashicorpVaultException(ioException.getMessage(), ioException));
  }
}
