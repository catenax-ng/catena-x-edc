/*
 *  Copyright (c) 2022 ZF Friedrichshafen AG
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       ZF Friedrichshafen AG - Initial Implementation
 *
 */

package org.eclipse.tractusx.edc.tests.util;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.tractusx.edc.tests.Environment;

public class S3Client {

  private final AmazonS3 s3;

  public S3Client(Environment environment) {
    s3 =
        AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(
                new EndpointConfiguration(
                    environment.getAwsEndpointOverride(), Regions.US_EAST_1.getName()))
            .withCredentials(new EnvironmentVariableCredentialsProvider())
            .build();
  }

  public void createBucket(String bucketName) {
    s3.createBucket(bucketName);
  }

  public void createFile(String bucketName, String fileName) throws IOException {
    File tempFile = File.createTempFile(fileName, null);
    Files.write(
        tempFile.toPath(), "Will fail if the file has no content".getBytes(StandardCharsets.UTF_8));
    s3.putObject(bucketName, fileName, tempFile);
  }

  public List<String> listBuckets() {
    return s3.listBuckets().stream().map(Bucket::getName).collect(Collectors.toList());
  }

  public Set<String> listBucketContent(String bucketName) {
    return s3.listObjects(bucketName).getObjectSummaries().stream()
        .map(S3ObjectSummary::getKey)
        .collect(Collectors.toSet());
  }

  public void deleteAllBuckets() {
    List<Bucket> buckets = s3.listBuckets();
    buckets.forEach(this::clearBucket);
    buckets.forEach(bucket -> s3.deleteBucket(bucket.getName()));
  }

  private void clearBucket(Bucket bucket) {
    String bucketName = bucket.getName();
    s3.listObjects(bucketName)
        .getObjectSummaries()
        .forEach(objectSummary -> s3.deleteObject(bucketName, objectSummary.getKey()));
  }
}
