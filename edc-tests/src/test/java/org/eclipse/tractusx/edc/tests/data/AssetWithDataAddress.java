package org.eclipse.tractusx.edc.tests.data;

import lombok.NonNull;
import lombok.Value;

@Value
public class AssetWithDataAddress {

  @NonNull Asset asset;

  @NonNull DataAddress dataAddress;
}
