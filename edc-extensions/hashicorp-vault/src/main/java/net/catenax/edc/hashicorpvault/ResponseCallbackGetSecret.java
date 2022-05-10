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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.eclipse.dataspaceconnector.spi.result.Result;
import org.jetbrains.annotations.NotNull;

class ResponseCallbackGetSecret extends ResponseCallback<Result<String>> {
  private final ObjectMapper objectMapper;
  private final String vaultDataEntryName;
  private static final String ERROR_TEMPLATE = "Call unsuccessful: %s";

  public ResponseCallbackGetSecret(
      @NonNull CompletableFuture<Result<String>> completableFuture,
      ObjectMapper objectMapper,
      String vaultDataEntryName) {
    super(completableFuture);
    this.objectMapper = objectMapper;
    this.vaultDataEntryName = vaultDataEntryName;
  }

  @Override
  public void onResponse(@NotNull Call call, @NotNull Response response) {

    if (response.code() == 404) {
      completableFuture.complete(null);
      return;
    }

    if (response.isSuccessful()) {
      try (ResponseBody body = response.body()) {
        if (body == null) {
          completableFuture.completeExceptionally(
              new HashicorpVaultException("Received an empty body response from vault"));
          return;
        }

        HashicorpVaultGetEntryResponsePayload payload =
            objectMapper.readValue(body.string(), HashicorpVaultGetEntryResponsePayload.class);

        String value = Objects.requireNonNull(payload.getData().getData().get(vaultDataEntryName));

        completableFuture.complete(Result.success(value));
      } catch (Exception exception) {
        completableFuture.completeExceptionally(exception);
      }
      return;
    }

    completableFuture.complete(Result.failure(String.format(ERROR_TEMPLATE, response.code())));
  }
}
