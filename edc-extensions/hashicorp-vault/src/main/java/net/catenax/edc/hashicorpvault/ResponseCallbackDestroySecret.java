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

import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import okhttp3.Call;
import okhttp3.Response;
import org.eclipse.dataspaceconnector.spi.result.Result;
import org.jetbrains.annotations.NotNull;

class ResponseCallbackDestroySecret extends ResponseCallback<Result<Void>> {
  private static final String ERROR_TEMPLATE = "Call unsuccessful: %s";

  public ResponseCallbackDestroySecret(@NonNull CompletableFuture<Result<Void>> completableFuture) {
    super(completableFuture);
  }

  @Override
  public void onResponse(@NotNull Call call, @NotNull Response response) {

    if (response.isSuccessful() || response.code() == 404) {
      completableFuture.complete(Result.success());

      return;
    }

    completableFuture.complete(Result.failure(String.format(ERROR_TEMPLATE, response.code())));
  }
}
