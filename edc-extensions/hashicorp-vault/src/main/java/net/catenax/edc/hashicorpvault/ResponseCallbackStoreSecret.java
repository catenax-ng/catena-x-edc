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
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.eclipse.dataspaceconnector.spi.result.Result;
import org.jetbrains.annotations.NotNull;

class ResponseCallbackStoreSecret
    extends ResponseCallback<Result<HashicorpVaultCreateEntryResponsePayload>> {
  private final ObjectMapper objectMapper;
  private static final String ERROR_TEMPLATE = "Call unsuccessful: %s";

  public ResponseCallbackStoreSecret(
      @NonNull
          CompletableFuture<Result<HashicorpVaultCreateEntryResponsePayload>> completableFuture,
      ObjectMapper objectMapper) {
    super(completableFuture);
    this.objectMapper = objectMapper;
  }

  @Override
  public void onResponse(@NotNull Call call, @NotNull Response response) {
    if (response.isSuccessful()) {
      try (ResponseBody responseBody = response.body()) {
        if (responseBody == null) {
          completableFuture.completeExceptionally(
              new HashicorpVaultException("Received an empty body response from vault"));

          return;
        }

        HashicorpVaultCreateEntryResponsePayload responsePayload =
            objectMapper.readValue(
                responseBody.string(), HashicorpVaultCreateEntryResponsePayload.class);

        completableFuture.complete(Result.success(responsePayload));
      } catch (Exception exception) {
        completableFuture.completeExceptionally(exception);
      }
      return;
    }

    completableFuture.complete(Result.failure(String.format(ERROR_TEMPLATE, response.code())));
  }
}
