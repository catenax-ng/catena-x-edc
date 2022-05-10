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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.eclipse.dataspaceconnector.spi.result.Result;

@RequiredArgsConstructor
class HashicorpVaultClient {
  static final String VAULT_DATA_ENTRY_NAME = "content";
  private static final String VAULT_TOKEN_HEADER = "X-Vault-Token";
  private static final String VAULT_REQUEST_HEADER = "X-Vault-Request";
  private static final MediaType MEDIA_TYPE_APPLICATION_JSON = MediaType.get("application/json");
  private static final String VAULT_API_VERSION = "v1";
  private static final String VAULT_SECRET_PATH = "secret";
  private static final String VAULT_SECRET_DATA_PATH = "data";
  private static final String VAULT_SECRET_METADATA_PATH = "metadata";
  private static final String CALL_UNKNOWN_ERROR_TEMPLATE = "Call unsuccessful: %s";
  @NonNull private final HashicorpVaultClientConfig config;
  @NonNull private final OkHttpClient okHttpClient;
  @NonNull private final ObjectMapper objectMapper;

  Result<String> getSecretValue(@NonNull String key) {
    key = URLEncoder.encode(key, StandardCharsets.UTF_8);
    String requestURI = getSecretDataUrl(key);
    Headers.Builder headersBuilder =
        new Headers.Builder().add(VAULT_REQUEST_HEADER, Boolean.toString(true));
    if (config.getVaultToken() != null) {
      headersBuilder = headersBuilder.add(VAULT_TOKEN_HEADER, config.getVaultToken());
    }
    Headers headers = headersBuilder.build();
    Request request = new Request.Builder().url(requestURI).headers(headers).get().build();

    Response response;
    try {
      response = okHttpClient.newCall(request).execute();
    } catch (IOException e) {
      throw new HashicorpVaultException(e.getMessage(), e);
    }

    if (response.code() == 404) {
      return null;
    }

    if (response.isSuccessful()) {
      try (ResponseBody body = response.body()) {
        if (body == null) {
          return Result.failure("Received an empty body response from vault");
        }

        HashicorpVaultGetEntryResponsePayload payload =
            objectMapper.readValue(body.string(), HashicorpVaultGetEntryResponsePayload.class);

        String value =
            Objects.requireNonNull(payload.getData().getData().get(VAULT_DATA_ENTRY_NAME));

        return Result.success(value);
      } catch (Exception e) {
        return Result.failure(String.format("Error unpacking response: %s", e.getMessage()));
      }
    }
    return Result.failure(String.format(CALL_UNKNOWN_ERROR_TEMPLATE, response.code()));
  }

  Result<HashicorpVaultCreateEntryResponsePayload> setSecret(
      @NonNull String key, @NonNull String value) {
    key = URLEncoder.encode(key, StandardCharsets.UTF_8);
    String requestURI = getSecretDataUrl(key);
    Headers.Builder headersBuilder =
        new Headers.Builder().add(VAULT_REQUEST_HEADER, Boolean.toString(true));
    if (config.getVaultToken() != null) {
      headersBuilder = headersBuilder.add(VAULT_TOKEN_HEADER, config.getVaultToken());
    }
    Headers headers = headersBuilder.build();
    HashicorpVaultCreateEntryRequestPayload requestPayload =
        HashicorpVaultCreateEntryRequestPayload.builder()
            .data(Collections.singletonMap(VAULT_DATA_ENTRY_NAME, value))
            .build();
    Request request =
        new Request.Builder()
            .url(requestURI)
            .headers(headers)
            .post(createRequestBody(requestPayload))
            .build();

    Response response;
    try {
      response = okHttpClient.newCall(request).execute();
    } catch (IOException e) {
      throw new HashicorpVaultException(e.getMessage(), e);
    }

    if (response.isSuccessful()) {
      try (ResponseBody responseBody = response.body()) {
        if (responseBody == null) {
          return Result.failure("Received an empty body response from vault");
        }

        HashicorpVaultCreateEntryResponsePayload responsePayload =
            objectMapper.readValue(
                responseBody.string(), HashicorpVaultCreateEntryResponsePayload.class);

        return Result.success(responsePayload);
      } catch (Exception e) {
        return Result.failure(String.format("Error unpacking response: %s", e.getMessage()));
      }
    }
    return Result.failure(String.format(CALL_UNKNOWN_ERROR_TEMPLATE, response.code()));
  }

  Result<Void> destroySecret(@NonNull String key) {
    key = URLEncoder.encode(key, StandardCharsets.UTF_8);
    String requestURI = getSecretMetadataUrl(key);
    Headers.Builder headersBuilder =
        new Headers.Builder().add(VAULT_REQUEST_HEADER, Boolean.toString(true));
    if (config.getVaultToken() != null) {
      headersBuilder = headersBuilder.add(VAULT_TOKEN_HEADER, config.getVaultToken());
    }
    Headers headers = headersBuilder.build();
    Request request = new Request.Builder().url(requestURI).headers(headers).delete().build();

    Response response;
    try {
      response = okHttpClient.newCall(request).execute();
    } catch (IOException e) {
      throw new HashicorpVaultException(e.getMessage(), e);
    }

    if (response.isSuccessful() || response.code() == 404) {
      return Result.success();
    }
    return Result.failure(String.format(CALL_UNKNOWN_ERROR_TEMPLATE, response.code()));
  }

  private String getBaseUrl() {
    String baseUrl = config.getVaultUrl();

    if (baseUrl.endsWith("/")) {
      baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
    }

    return baseUrl;
  }

  private String getSecretDataUrl(String key) {
    return URI.create(
            String.format(
                "%s/%s/%s/%s/%s",
                getBaseUrl(), VAULT_API_VERSION, VAULT_SECRET_PATH, VAULT_SECRET_DATA_PATH, key))
        .toString();
  }

  private String getSecretMetadataUrl(String key) {
    return URI.create(
            String.format(
                "%s/%s/%s/%s/%s",
                getBaseUrl(),
                VAULT_API_VERSION,
                VAULT_SECRET_PATH,
                VAULT_SECRET_METADATA_PATH,
                key))
        .toString();
  }

  private RequestBody createRequestBody(Object requestPayload) {
    String jsonRepresentation;
    try {
      jsonRepresentation = objectMapper.writeValueAsString(requestPayload);
    } catch (JsonProcessingException e) {
      throw new HashicorpVaultException(e.getMessage(), e);
    }
    return RequestBody.create(jsonRepresentation, MEDIA_TYPE_APPLICATION_JSON);
  }
}
