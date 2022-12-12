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
 *       Mercedes-Benz Tech Innovation GmbH - Initial Test
 *
 */

package org.eclipse.tractusx.edc.transferprocess.sftp.client;

import lombok.RequiredArgsConstructor;
import org.eclipse.dataspaceconnector.dataplane.spi.pipeline.DataSource;
import org.eclipse.dataspaceconnector.dataplane.spi.pipeline.DataSourceFactory;
import org.eclipse.dataspaceconnector.spi.result.Result;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.DataFlowRequest;
import org.eclipse.tractusx.edc.transferprocess.sftp.common.EdcSftpException;
import org.eclipse.tractusx.edc.transferprocess.sftp.common.SftpDataAddress;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class SftpDataSourceFactory implements DataSourceFactory {
  @Override
  public boolean canHandle(DataFlowRequest request) {
    try {
      SftpDataAddress.fromDataAddress(request.getSourceDataAddress());
      return true;
    } catch (EdcSftpException e) {
      return false;
    }
  }

  @Override
  public @NotNull Result<Boolean> validate(DataFlowRequest request) {
    try {
      SftpDataAddress.fromDataAddress(request.getSourceDataAddress());
      return VALID;
    } catch (EdcSftpException e) {
      return Result.failure(String.format("Invalid dataflow request: %s", request.getId()));
    }
  }

  @Override
  public DataSource createSource(DataFlowRequest request) {
    SftpDataAddress source;
    try {
      source = SftpDataAddress.fromDataAddress(request.getSourceDataAddress());
    } catch (EdcSftpException e) {
      throw new EdcSftpException(String.format("Invalid DataFlowRequest: %s", request.getId()), e);
    }

    SftpClientConfig sftpClientConfig =
        SftpClientConfig.builder()
            .sftpUser(source.getSftpUser())
            .sftpLocation(source.getSftpLocation())
            .build();

    SftpClientWrapper sftpClientWrapper = new SftpClientWrapperImpl(sftpClientConfig);
    return new SftpDataSource(sftpClientWrapper);
  }
}
